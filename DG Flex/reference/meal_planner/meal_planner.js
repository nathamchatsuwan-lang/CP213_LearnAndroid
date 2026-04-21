// --- Meal Timing Planner ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-plan-btn');

    function formatTime(date) {
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}:${minutes}`;
    }

    function createMealPlan() {
        const resultDiv = document.getElementById('meal-plan-result');
        const wakeUpTimeInput = document.getElementById('wake-up-time').value;
        const bedTimeInput = document.getElementById('bed-time').value;
        const numMeals = parseInt(document.getElementById('num-meals').value);

        // --- Validation ---
        if (!wakeUpTimeInput || !bedTimeInput) {
            resultDiv.innerHTML = '<p class="error">กรุณาเลือกเวลาตื่นและเวลาเข้านอน</p>';
            return;
        }
        
        const [wakeHours, wakeMinutes] = wakeUpTimeInput.split(':').map(Number);
        const [bedHours, bedMinutes] = bedTimeInput.split(':').map(Number);

        const wakeUpDate = new Date();
        wakeUpDate.setHours(wakeHours, wakeMinutes, 0, 0);

        const bedDate = new Date();
        bedDate.setHours(bedHours, bedMinutes, 0, 0);

        // Handle overnight case
        if (bedDate <= wakeUpDate) {
            bedDate.setDate(bedDate.getDate() + 1);
        }

        const awakeDurationMinutes = (bedDate - wakeUpDate) / (1000 * 60);

        if (awakeDurationMinutes <= 0) {
             resultDiv.innerHTML = '<p class="error">เวลาเข้านอนต้องอยู่หลังเวลาตื่นนอน</p>';
            return;
        }

        const mealInterval = awakeDurationMinutes / numMeals;

        let resultHTML = `<h3 style="text-align:center; margin-top: 30px;">ตารางเวลามื้ออาหารตัวอย่าง (${numMeals} มื้อ)</h3><div class="result-table">`;
        
        let lastMealTime = new Date(wakeUpDate.getTime());

        for (let i = 1; i <= numMeals; i++) {
             // For the first meal, schedule it a bit after waking up. For others, use the interval.
            const mealTime = (i === 1) 
                ? new Date(wakeUpDate.getTime() + 30 * 60000) // 30 mins after waking
                : new Date(lastMealTime.getTime() + mealInterval * 60000);

            resultHTML += `
                 <div class="result-row">
                    <div class="result-label"><p>มื้อที่ ${i}</p></div>
                    <div class="result-value-box maintain" style="background-color: #fafafa;">
                        <p class="main-calories" style="color: #555;">${formatTime(mealTime)}</p>
                    </div>
                </div>
            `;
            lastMealTime = mealTime;
        }

        resultHTML += '</div>';
        resultDiv.innerHTML = resultHTML;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', createMealPlan);
    }
});