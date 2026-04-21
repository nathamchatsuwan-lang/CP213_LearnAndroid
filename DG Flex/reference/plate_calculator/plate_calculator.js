// --- Plate Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const unitSelect = document.getElementById('unit-select');
    const barSelect = document.getElementById('bar-weight');
    const plateOptionsDiv = document.getElementById('plate-options');
    const calculateBtn = document.getElementById('calculate-plates-btn');
    const resultDiv = document.getElementById('plate-result');

    const plates = {
        kg: [25, 20, 15, 10, 5, 2.5, 1.25, 0.5],
        lbs: [45, 35, 25, 10, 5, 2.5, 1]
    };
    const bars = {
        kg: [{ text: 'Standard Olympic (20 กก.)', value: 20 }, { text: 'Women\'s Olympic (15 กก.)', value: 15 }],
        lbs: [{ text: 'Standard Olympic (45 ปอนด์)', value: 45 }, { text: 'Women\'s Olympic (35 ปอนด์)', value: 35 }]
    };

    function setupInputs() {
        const unit = unitSelect.value;
        // Populate bar options
        barSelect.innerHTML = bars[unit].map(bar => `<option value="${bar.value}">${bar.text}</option>`).join('');
        // Populate plate options
        plateOptionsDiv.innerHTML = plates[unit].map(p => `
            <div class="plate-checkbox">
                <input type="checkbox" id="plate-${p}" value="${p}" checked>
                <label for="plate-${p}" style="margin-left: 5px;">${p} ${unit}</label>
            </div>
        `).join('');
    }

    function calculatePlates() {
        const unit = unitSelect.value;
        const targetWeight = parseFloat(document.getElementById('target-weight').value);
        const barWeight = parseFloat(barSelect.value);

        const availablePlates = Array.from(document.querySelectorAll('#plate-options input:checked')).map(cb => parseFloat(cb.value));
        availablePlates.sort((a, b) => b - a); // Sort heaviest to lightest

        // Validation
        if (isNaN(targetWeight) || targetWeight <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักเป้าหมาย</p>';
            return;
        }
        if (targetWeight < barWeight) {
            resultDiv.innerHTML = '<p class="error">น้ำหนักเป้าหมายต้องไม่น้อยกว่าน้ำหนักคาน</p>';
            return;
        }

        let weightNeededPerSide = (targetWeight - barWeight) / 2;
        if (weightNeededPerSide < 0) {
             resultDiv.innerHTML = '<p class="error">คำนวณน้ำหนักติดลบ กรุณาตรวจสอบข้อมูล</p>';
             return;
        }

        const platesForOneSide = [];
        for (const plate of availablePlates) {
            while (weightNeededPerSide >= plate) {
                platesForOneSide.push(plate);
                weightNeededPerSide -= plate;
            }
        }

        let resultHTML = '';
        if (weightNeededPerSide > 0.01) { // Check for remaining weight (small tolerance for float errors)
             resultHTML = `<p class="error" style="text-align:center;">ไม่สามารถจัดแผ่นน้ำหนักให้ได้พอดี (เหลือเศษข้างละ ${weightNeededPerSide.toFixed(2)} ${unit})</p>`;
        } else {
            const plateListHTML = platesForOneSide.length > 0 
                ? platesForOneSide.map(p => `<div class="plate-item">${p} ${unit}</div>`).join('')
                : '<p>ไม่ต้องใส่แผ่นเพิ่ม</p>';
            
            resultHTML = `
                <div class="plate-result-container">
                    <div class="side-plates">
                        <h4>ข้างซ้าย</h4>
                        ${plateListHTML}
                    </div>
                    <div class="side-plates">
                        <h4>ข้างขวา</h4>
                        ${plateListHTML}
                    </div>
                </div>
            `;
        }
        resultDiv.innerHTML = resultHTML;
    }
    
    unitSelect.addEventListener('change', setupInputs);
    calculateBtn.addEventListener('click', calculatePlates);

    // Initial setup
    setupInputs();
});