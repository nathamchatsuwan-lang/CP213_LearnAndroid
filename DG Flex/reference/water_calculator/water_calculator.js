// --- Daily Water Intake Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-water-btn');

    // Main function to calculate Water Intake
    function calculateWater() {
        const resultDiv = document.getElementById('water-result');
        const weightInput = parseFloat(document.getElementById('water-weight').value);
        const unit = document.getElementById('weight-units').value;

        // --- Validation ---
        if (isNaN(weightInput) || weightInput <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักให้ถูกต้อง</p>';
            return;
        }

        // --- Unit Conversion to KG ---
        const weightInKg = (unit === 'lbs') ? weightInput / 2.20462 : weightInput;

        // --- Calculations (35-50 ml per kg of body weight) ---
        const lowerIntakeMl = weightInKg * 35;
        const upperIntakeMl = weightInKg * 50;
        
        const lowerIntakeL = lowerIntakeMl / 1000;
        const upperIntakeL = upperIntakeMl / 1000;

        // --- Display result ---
        resultDiv.innerHTML = `
             <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>ปริมาณน้ำที่แนะนำต่อวันคือ:</p>
                    <p class="bmi-value normal" style="font-size: 2.5em;">${lowerIntakeL.toFixed(2)} - ${upperIntakeL.toFixed(2)}</p>
                    <p class="bmi-category normal" style="font-size: 1.1em;">ลิตร / วัน</p>
                    <p class="sub-text" style="color: #666; margin-top: 10px;">(หรือ ${lowerIntakeMl.toFixed(0)} - ${upperIntakeMl.toFixed(0)} มิลลิลิตร)</p>
                </div>
                <p style="text-align: center; font-size: 0.85em; color: #666; margin-top: 15px;">*ความต้องการน้ำอาจเพิ่มขึ้นตามการออกกำลังกายและสภาพอากาศ*</p>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateWater);
    }
});