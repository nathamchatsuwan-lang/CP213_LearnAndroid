// --- Sleep Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-sleep-btn');

    function formatTime(date) {
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}:${minutes}`;
    }

    function calculateBedtimes() {
        const resultDiv = document.getElementById('sleep-result');
        const wakeUpTimeInput = document.getElementById('wake-up-time').value;

        // --- Validation ---
        if (!wakeUpTimeInput) {
            resultDiv.innerHTML = '<p class="error">กรุณาเลือกเวลาที่ต้องการตื่นนอน</p>';
            return;
        }
        
        const [hours, minutes] = wakeUpTimeInput.split(':').map(Number);
        
        const wakeUpDate = new Date();
        wakeUpDate.setHours(hours);
        wakeUpDate.setMinutes(minutes);
        wakeUpDate.setSeconds(0);

        const TIME_TO_FALL_ASLEEP = 15; // minutes
        const SLEEP_CYCLE_DURATION = 90; // minutes
        const CYCLES_TO_CALCULATE = [6, 5, 4]; // Corresponds to 9, 7.5, 6 hours of sleep

        let resultHTML = `<p style="text-align:center; font-weight: bold;">เพื่อให้คุณตื่นตอน ${formatTime(wakeUpDate)} อย่างสดชื่น, คุณควรเข้านอนในเวลาประมาณ:</p>`;

        for (const cycles of CYCLES_TO_CALCULATE) {
            const totalSleepTime = cycles * SLEEP_CYCLE_DURATION;
            
            // Create a new date object for calculation to avoid modifying the original
            const bedtime = new Date(wakeUpDate.getTime());
            bedtime.setMinutes(bedtime.getMinutes() - totalSleepTime - TIME_TO_FALL_ASLEEP);

            resultHTML += `
                <div class="bedtime-option">
                    ${formatTime(bedtime)}
                    <div class="sub-text">สำหรับ ${cycles} รอบการนอนหลับ (${totalSleepTime / 60} ชั่วโมง)</div>
                </div>
            `;
        }
        
        resultHTML += '<p style="text-align: center; font-size: 0.85em; color: #666; margin-top: 15px;">*คำนวณโดยเผื่อเวลาให้คุณเผลอหลับประมาณ 15 นาที*</p>';
        resultDiv.innerHTML = resultHTML;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateBedtimes);
    }
});