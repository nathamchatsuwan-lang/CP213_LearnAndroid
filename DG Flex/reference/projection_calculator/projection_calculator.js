// --- Weight Change Projection Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-proj-btn');

    function calculateProjection() {
        const resultDiv = document.getElementById('proj-result');
        const unit = document.getElementById('proj-unit').value;
        const currentWeight = parseFloat(document.getElementById('current-weight').value);
        const goalWeight = parseFloat(document.getElementById('goal-weight').value);
        const tdee = parseFloat(document.getElementById('proj-tdee').value);
        const planCalories = parseFloat(document.getElementById('plan-calories').value);

        // --- Validation ---
        if (isNaN(currentWeight) || isNaN(goalWeight) || isNaN(tdee) || isNaN(planCalories)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
            return;
        }
        if (currentWeight <= 0 || goalWeight <= 0 || tdee <= 0 || planCalories <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขที่มากกว่า 0</p>';
            return;
        }
        if (currentWeight === goalWeight) {
             resultDiv.innerHTML = '<p class="error">น้ำหนักปัจจุบันและเป้าหมายเป็นค่าเดียวกัน</p>';
            return;
        }

        const dailyCalorieDiff = tdee - planCalories;

        if ((goalWeight < currentWeight && dailyCalorieDiff <= 0) || (goalWeight > currentWeight && dailyCalorieDiff >= 0)) {
            resultDiv.innerHTML = '<p class="error">แผนการกินของคุณไม่สอดคล้องกับเป้าหมาย (เช่น ต้องการลดน้ำหนักแต่กินแคลอรี่เกิน TDEE)</p>';
            return;
        }

        // --- Calculations ---
        let currentWeightKg = (unit === 'lbs') ? currentWeight / 2.20462 : currentWeight;
        let goalWeightKg = (unit === 'lbs') ? goalWeight / 2.20462 : goalWeight;
        
        const weightToChangeKg = Math.abs(currentWeightKg - goalWeightKg);
        const totalCalorieToChange = weightToChangeKg * 7700; // Approx. 7700 kcal per 1 kg of fat/muscle
        
        const daysNeeded = totalCalorieToChange / Math.abs(dailyCalorieDiff);

        // --- Display result ---
        let goalType = (goalWeight < currentWeight) ? 'ลดน้ำหนัก' : 'เพิ่มน้ำหนัก';
        resultDiv.innerHTML = `
             <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>เพื่อ${goalType} ${weightToChangeKg.toFixed(1)} ${unit} คุณจะใช้เวลาประมาณ:</p>
                    <p class="bmi-value normal">${Math.round(daysNeeded)}</p>
                    <p class="bmi-category normal">วัน</p>
                </div>
            </div>
        `;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateProjection);
    }
});