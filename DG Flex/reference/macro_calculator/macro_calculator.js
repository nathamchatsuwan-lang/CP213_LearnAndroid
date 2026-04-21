// --- Macronutrient Split Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-macro-btn');
    const goalSelect = document.getElementById('macro-goal');
    const customGroup = document.getElementById('custom-ratio-group');

    // Show/hide custom inputs based on selection
    goalSelect.addEventListener('change', () => {
        if (goalSelect.value === 'custom') {
            customGroup.style.display = 'block';
        } else {
            customGroup.style.display = 'none';
        }
    });

    // Main function to calculate Macros
    function calculateMacros() {
        const resultDiv = document.getElementById('macro-result');
        const totalCalories = parseFloat(document.getElementById('macro-calories').value);
        const goal = document.getElementById('macro-goal').value;

        // --- Validation ---
        if (isNaN(totalCalories) || totalCalories <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกพลังงานรวม (แคลอรี่) ให้ถูกต้อง</p>';
            return;
        }

        let percentages = {};
        if (goal === 'balanced') {
            percentages = { carbs: 40, protein: 30, fat: 30 };
        } else if (goal === 'low-carb') {
            percentages = { carbs: 25, protein: 45, fat: 30 };
        } else if (goal === 'high-protein') {
            percentages = { carbs: 30, protein: 40, fat: 30 };
        } else if (goal === 'custom') {
            const carbsP = parseFloat(document.getElementById('custom-carbs').value) || 0;
            const proteinP = parseFloat(document.getElementById('custom-protein').value) || 0;
            const fatP = parseFloat(document.getElementById('custom-fat').value) || 0;
            
            if (carbsP + proteinP + fatP !== 100) {
                resultDiv.innerHTML = '<p class="error">สัดส่วนที่กำหนดเองต้องรวมกันได้ 100%</p>';
                return;
            }
            percentages = { carbs: carbsP, protein: proteinP, fat: fatP };
        }
        
        // --- Calculations ---
        const proteinGrams = (totalCalories * (percentages.protein / 100)) / 4;
        const carbsGrams = (totalCalories * (percentages.carbs / 100)) / 4;
        const fatGrams = (totalCalories * (percentages.fat / 100)) / 9;

        const proteinCalories = proteinGrams * 4;
        const carbsCalories = carbsGrams * 4;
        const fatCalories = fatGrams * 9;

        // --- Display result ---
        resultDiv.innerHTML = `
            <h3 style="text-align:center; margin-top: 30px;">ผลลัพธ์สำหรับ ${Math.round(totalCalories)} แคลอรี่</h3>
            <div class="result-table">
                <div class="result-row">
                    <div class="result-label"><p>โปรตีน</p><p class="sub-text">${percentages.protein}%</p></div>
                    <div class="result-value-box loss">
                        <p class="main-calories">${proteinGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                        <p class="sub-text">${proteinCalories.toFixed(0)} แคลอรี่</p>
                    </div>
                </div>
                <div class="result-row">
                    <div class="result-label"><p>คาร์โบไฮเดรต</p><p class="sub-text">${percentages.carbs}%</p></div>
                    <div class="result-value-box maintain" style="background-color: #e6f7ff;">
                        <p class="main-calories" style="color: #1d39c4;">${carbsGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                        <p class="sub-text">${carbsCalories.toFixed(0)} แคลอรี่</p>
                    </div>
                </div>
                <div class="result-row">
                    <div class="result-label"><p>ไขมัน</p><p class="sub-text">${percentages.fat}%</p></div>
                    <div class="result-value-box gain">
                        <p class="main-calories">${fatGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                        <p class="sub-text">${fatCalories.toFixed(0)} แคลอรี่</p>
                    </div>
                </div>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateMacros);
    }
});