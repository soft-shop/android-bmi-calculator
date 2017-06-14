package biz.softshop.bmicalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the references to the widgets
        final EditText weightField = (EditText) findViewById(R.id.weightField);
        final EditText heightField = (EditText) findViewById(R.id.heightField);
        final TextView bmiLabel = (TextView) findViewById(R.id.bmiLabel);
        final TextView bmiCatLabel = (TextView) findViewById(R.id.bmiCatLabel);

        findViewById(R.id.calculateButton).setOnClickListener(new View.OnClickListener() {

            // Logic for validation, input can't be empty
            @Override
            public void onClick(View v) {

                String weight = weightField.getText().toString();
                String height = heightField.getText().toString();

                if (TextUtils.isEmpty(weight)) {
                    weightField.setError("Please enter your weight");
                    weightField.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(height)) {
                    heightField.setError("Please enter your height");
                    heightField.requestFocus();
                    return;
                }

                //Get the user values from the widget reference
                float weightVal = Float.parseFloat(weight);
                float heightVal = Float.parseFloat(height) / 100;

                //Calculate BMI value
                float bmiValue = calculateBMI(weightVal, heightVal);

                //Define the meaning of the bmi value
                String bmiInterpretation = interpretBMI(bmiValue);

                bmiLabel.setText(String.valueOf(bmiValue));
                bmiCatLabel.setText(bmiInterpretation);
            }
        });

    }

    //Calculate BMI
    private float calculateBMI(float weight, float height) {
        return (float) (weight / (height * height));
    }

    // Interpret what BMI means
    private String interpretBMI(float bmiValue) {
        if (bmiValue < 16) {
            return "Severely underweight";
        } else if (bmiValue < 18.5) {

            return "Underweight";
        } else if (bmiValue < 25) {

            return "Normal";
        } else if (bmiValue < 30) {

            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
