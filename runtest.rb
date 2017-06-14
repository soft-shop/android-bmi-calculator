require 'aws-sdk'
require 'json'
require 'net/http'

def upload(url, file_path)
    url = URI.parse(url)
    apk_contents = File.open(file_path, 'rb').read
    Net::HTTP.start(url.host) do |http|
        http.send_request('PUT', url.request_uri, apk_contents, {'content-type' => 'application/octet-stream'})
    end
end

def publish_to_sns(msg, subject) 
    sns = Aws::SNS::Client.new(region: 'us-east-1')
    sns.publish(
        topic_arn: ENV['SNS_TOPIC_ARN'],
        subject: subject,
        message: msg
    )
end

def send_slack_alert(text)
    uri = URI.parse(ENV['SLACK_WEB_HOOK_URL'])
    data = {"text" => text}
    Net::HTTP.post_form(uri, {"payload" => data.to_json})
end

def get_summary(run_resp) 
    summary =  "\n\nTotal tests executed  : #{run_resp.run.counters.total}\n" +
        "Passed                : #{run_resp.run.counters.passed}\n" +
        "Failed                : #{run_resp.run.counters.failed}\n" +
        "Warned                : #{run_resp.run.counters.warned}\n" +
        "Errored               : #{run_resp.run.counters.errored}\n" +
        "Stopped               : #{run_resp.run.counters.stopped}\n" +
        "Skipped               : #{run_resp.run.counters.skipped}\n\n"
    return summary
end

project_arn = ENV['PROJECT_ARN']

devicefarm = Aws::DeviceFarm::Client.new(
    region: 'us-west-2'
)

list_device_pools_resp = devicefarm.list_device_pools({
    arn: project_arn
})

upload_apk_resp = devicefarm.create_upload({
    project_arn: project_arn,
    name: 'app-release.apk',
    type: 'ANDROID_APP',
    content_type: 'application/octet-stream'
})

apk_path = 'bmi-calc-app/app/build/outputs/apk/app-debug.apk'

upload(upload_apk_resp.upload.url, apk_path)

upload_appium_test_resp = devicefarm.create_upload({
    project_arn: project_arn,
    name: 'tests.zip',
    type: 'APPIUM_JAVA_TESTNG_TEST_PACKAGE',
    content_type: 'application/octet-stream'
})

upload(upload_appium_test_resp.upload.url, 'mobile-tests/target/zip-with-dependencies.zip')

10.times do
    res_apk_upload = devicefarm.get_upload({
        arn: upload_apk_resp.upload.arn
    })

    res_test_upload = devicefarm.get_upload({
        arn: upload_appium_test_resp.upload.arn
    })

    if res_apk_upload.upload.status == 'SUCCEEDED' &&
        res_test_upload.upload.status == 'SUCCEEDED'

        schedule_run_resp = devicefarm.schedule_run({
            project_arn: project_arn,
            app_arn: upload_apk_resp.upload.arn,
            device_pool_arn: list_device_pools_resp.device_pools[1].arn,
            name: 'Mobile Tests',
            test: {
                type: 'APPIUM_JAVA_TESTNG',
                test_package_arn: upload_appium_test_resp.upload.arn
            }
        })
        puts 'Started a new run for mobile-tests'
        send_slack_alert("Started a new run for mobile-tests")
        run_arn = schedule_run_resp.run.arn
        sleep(20)

        puts 'Waiting for run to complete on AWS Device Farm'

        begin
            sleep(10)
            run_resp = devicefarm.get_run({
                arn: run_arn,
            })
            if run_resp.run.status == 'COMPLETED'
                puts "RESULT: #{run_resp.run.result}"
                if run_resp.run.result == 'PASSED'
                    result = true
                else
                    result = false
                end
            end
        end until run_resp.run.status == 'COMPLETED'

         project_id, run_id = run_arn.scan(/([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12})/)

         result_url = "https://us-west-2.console.aws.amazon.com/devicefarm/home?region=us-west-2#/projects/#{project_id[0]}/runs/#{run_id[0]}"

        if result
            summary = get_summary(run_resp)
            puts summary
            puts ''
            puts "See results at: #{result_url}"
            summary = get_summary(run_resp)
            msg = "All tests passed #{summary}See results at: #{result_url}"
            publish_to_sns(msg, 'Mobile Tests Passed')
            send_slack_alert("All tests passed " + summary)
            send_slack_alert("See results at: " + result_url)
            exit
        else
            summary = get_summary(run_resp)
            puts summary
            puts ''
            puts "See results at: #{result_url}"
            msg = "Test run has failures #{summary}See results at: #{result_url}"
            publish_to_sns(msg, 'Mobile Tests Failed')
            send_slack_alert("Test run has failures " + summary)
            send_slack_alert("See results at: " + result_url)
            exit 1
        end
    end
    sleep(1)
end



