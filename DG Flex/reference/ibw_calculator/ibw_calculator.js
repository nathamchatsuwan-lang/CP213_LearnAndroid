// --- Ideal Body Weight (IBW) Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-ibw-btn');

    // Main function to calculate IBW
    function calculateIBW() {
        const gender = document.querySelector('input[name="gender"]:checked').value;
        const units = document.getElementById('units').value;
        const resultDiv = document.getElementById('ibw-result');
        let heightInput = parseFloat(document.getElementById('ibw-height').value);

        // --- Validation ---
        if (isNaN(heightInput) || heightInput <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกส่วนสูงให้ถูกต้อง</p>';
            return;
        }

        // --- Unit Conversion ---
        let heightInCm = (units === 'in') ? heightInput * 2.54 : heightInput;
        let heightInMeters = heightInCm / 100;
        let heightInInches = heightInCm / 2.54;

        // --- Calculations ---
        let hamwi, robinson, miller, bmiRange;
        const inchesOver5Feet = (heightInInches > 60) ? heightInInches - 60 : 0;

        if (gender === 'male') {
            hamwi = 48.0 + (2.7 * inchesOver5Feet);
            robinson = 52.0 + (1.9 * inchesOver5Feet);
            miller = 56.2 + (1.41 * inchesOver5Feet);
        } else { // female
            hamwi = 45.5 + (2.2 * inchesOver5Feet);
            robinson = 49.0 + (1.7 * inchesOver5Feet);
            miller = 53.1 + (1.36 * inchesOver5Feet);
        }

        const bmiLow = 18.5 * (heightInMeters * heightInMeters);
        const bmiHigh = 24.9 * (heightInMeters * heightInMeters);
        bmiRange = `${bmiLow.toFixed(1)} - ${bmiHigh.toFixed(1)}`;
        
        // --- Display result ---
        resultDiv.innerHTML = `
            <div class="result-table" style="margin-top: 30px;">
                <div class="result-row">
                    <div class="result-label"><p>สูตร Hamwi (1964)</p></div>
                    <div class="result-value-box maintain" style="background-color: #e6f7ff;">
                        <p class="main-calories" style="color: #1d39c4;">${hamwi.toFixed(1)} <span style="font-size: 0.5em; font-weight: normal;">กก.</span></p>
                    </div>
                </div>
                 <div class="result-row">
                    <div class="result-label"><p>สูตร Robinson (1983)</p></div>
                    <div class="result-value-box maintain" style="background-color: #e6f7ff;">
                        <p class="main-calories" style="color: #1d39c4;">${robinson.toFixed(1)} <span style="font-size: 0.5em; font-weight: normal;">กก.</span></p>
                    </div>
                </div>
                 <div class="result-row">
                    <div class="result-label"><p>สูตร Miller (1983)</p></div>
                    <div class="result-value-box maintain" style="background-color: #e6f7ff;">
                        <p class="main-calories" style="color: #1d39c4;">${miller.toFixed(1)} <span style="font-size: 0.5em; font-weight: normal;">กก.</span></p>
                    </div>
                </div>
                 <div class="result-row">
                    <div class="result-label"><p>ช่วงน้ำหนักสุขภาพดี (BMI 18.5-24.9)</p></div>
                    <div class="result-value-box maintain" style="background-color: #e8f5e9;">
                        <p class="main-calories" style="color: #2e7d32;">${bmiRange} <span style="font-size: 0.5em; font-weight: normal;">กก.</span></p>
                    </div>
                </div>
            </div>
            <p style="text-align: center; font-size: 0.85em; color: #666; margin-top: 15px;">*ค่าที่ได้เป็นเพียงการประมาณการ ไม่ได้คำนึงถึงมวลกล้ามเนื้อและไขมัน*</p>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateIBW);
    }
});