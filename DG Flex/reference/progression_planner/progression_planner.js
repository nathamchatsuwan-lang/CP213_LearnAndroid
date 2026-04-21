// --- Load Progression Planner ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-plan-btn');

    function createPlan() {
        const resultDiv = document.getElementById('plan-result');
        const currentWeight = parseFloat(document.getElementById('current-weight').value);
        const weeks = parseInt(document.getElementById('plan-weeks').value);
        const increment = parseFloat(document.getElementById('increment-weight').value);
        const unit = document.getElementById('weight-unit').value;

        // --- Validation ---
        if (isNaN(currentWeight) || isNaN(weeks) || isNaN(increment)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
            return;
        }
        if (currentWeight <= 0 || weeks <= 0 || increment < 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขให้ถูกต้อง (น้ำหนักและสัปดาห์ต้องมากกว่า 0)</p>';
            return;
        }
        if (weeks > 52) {
             resultDiv.innerHTML = '<p class="error">สามารถวางแผนได้สูงสุดครั้งละ 52 สัปดาห์</p>';
            return;
        }

        // --- Generate Plan Table ---
        let tableHTML = `
            <h3 style="text-align:center; margin-top: 30px;">แผนการเพิ่มน้ำหนัก ${weeks} สัปดาห์</h3>
            <div class="result-table">
        `;
        
        for (let i = 1; i <= weeks; i++) {
            const targetWeight = currentWeight + ((i - 1) * increment);
            tableHTML += `
                 <div class="result-row">
                    <div class="result-label"><p>สัปดาห์ที่ ${i}</p></div>
                    <div class="result-value-box maintain" style="background-color: #fafafa;">
                        <p class="main-calories" style="color: #555;">${targetWeight.toFixed(2)} <span style="font-size: 0.5em; font-weight: normal;">${unit}</span></p>
                    </div>
                </div>
            `;
        }

        tableHTML += '</div>';
        resultDiv.innerHTML = tableHTML;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', createPlan);
    }
});