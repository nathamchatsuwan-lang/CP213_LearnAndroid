// --- Daily Protein Intake Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-protein-btn');

    // Main function to calculate Protein Intake
    function calculateProtein() {
        const resultDiv = document.getElementById('protein-result');
        const weightInput = parseFloat(document.getElementById('protein-weight').value);
        const unit = document.getElementById('weight-units').value;
        const goal = document.getElementById('activity-goal').value;

        // --- Validation ---
        if (isNaN(weightInput) || weightInput <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักให้ถูกต้อง</p>';
            return;
        }

        // --- Unit Conversion to KG ---
        const weightInKg = (unit === 'lbs') ? weightInput / 2.20462 : weightInput;

        // --- Determine Multiplier Range ---
        let lowerMultiplier, upperMultiplier;
        switch(goal) {
            case 'sedentary':
                lowerMultiplier = 0.8;
                upperMultiplier = 1.0;
                break;
            case 'active':
                lowerMultiplier = 1.2;
                upperMultiplier = 1.6;
                break;
            case 'muscle-gain':
                lowerMultiplier = 1.6;
                upperMultiplier = 2.2;
                break;
            case 'fat-loss':
                lowerMultiplier = 1.8;
                upperMultiplier = 2.4;
                break;
            default:
                lowerMultiplier = 1.2;
                upperMultiplier = 1.6;
        }
        
        // --- Calculations ---
        const lowerIntake = weightInKg * lowerMultiplier;
        const upperIntake = weightInKg * upperMultiplier;

        // --- Display result ---
        resultDiv.innerHTML = `
             <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>ปริมาณโปรตีนที่คุณต้องการต่อวันคือ:</p>
                    <p class="bmi-value normal">${lowerIntake.toFixed(0)} - ${upperIntake.toFixed(0)}</p>
                    <p class="bmi-category normal">กรัม / วัน</p>
                </div>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateProtein);
    }
});