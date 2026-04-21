// --- 1RM (One Rep Max) Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-onerm-btn');

    function calculate1RM() {
        const resultDiv = document.getElementById('onerm-result');
        const weight = parseFloat(document.getElementById('onerm-weight').value);
        const reps = parseInt(document.getElementById('onerm-reps').value);
        const unit = document.getElementById('weight-units').value;

        // --- Validation ---
        if (isNaN(weight) || isNaN(reps)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
            return;
        }
        if (weight <= 0 || reps <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขที่มากกว่า 0</p>';
            return;
        }
         if (reps > 12) {
            resultDiv.innerHTML = '<p class="error">เพื่อความแม่นยำ ควรใช้จำนวนครั้งไม่เกิน 12 reps</p>';
            return;
        }
        if (reps === 1) {
            resultDiv.innerHTML = '<p class="error">ถ้าทำได้ 1 ครั้ง นั่นคือ 1RM ของคุณแล้ว ไม่จำเป็นต้องคำนวณ</p>';
            return;
        }

        // --- Epley Formula Calculation ---
        const oneRepMax = weight * (1 + (reps / 30));

        // --- Generate Percentage Table ---
        const percentages = [95, 90, 85, 80, 75, 70, 65, 60];
        let tableHTML = `
            <h3 style="text-align:center; margin-top: 30px;">ค่าประมาณ 1RM ของคุณ</h3>
            <div class="bmi-result-display" style="margin-bottom: 20px;">
                 <p class="bmi-value normal" style="font-size: 2.8em;">${oneRepMax.toFixed(1)}</p>
                 <p class="bmi-category normal">${unit}</p>
            </div>
            <h4 style="text-align:center;">ตารางน้ำหนักสำหรับฝึกซ้อม (% ของ 1RM)</h4>
            <div class="result-table">
        `;
        
        for (const p of percentages) {
            const calculatedWeight = oneRepMax * (p / 100);
            tableHTML += `
                 <div class="result-row">
                    <div class="result-label"><p>${p}%</p></div>
                    <div class="result-value-box maintain" style="background-color: #fafafa;">
                        <p class="main-calories" style="color: #555;">${calculatedWeight.toFixed(1)} <span style="font-size: 0.5em; font-weight: normal;">${unit}</span></p>
                    </div>
                </div>
            `;
        }

        tableHTML += '</div>';
        resultDiv.innerHTML = tableHTML;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculate1RM);
    }
});