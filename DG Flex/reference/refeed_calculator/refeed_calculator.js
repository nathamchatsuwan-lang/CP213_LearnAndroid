// --- Refeed Day Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-refeed-btn');

    // Main function to calculate Refeed Day Macros
    function calculateRefeed() {
        const resultDiv = document.getElementById('refeed-result');
        const tdee = parseFloat(document.getElementById('refeed-tdee').value);
        const proteinGrams = parseFloat(document.getElementById('refeed-protein').value);
        const fatGrams = parseFloat(document.getElementById('refeed-fat').value);

        // --- Validation ---
        if (isNaN(tdee) || isNaN(proteinGrams) || isNaN(fatGrams)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
            return;
        }
        if (tdee <= 0 || proteinGrams <= 0 || fatGrams <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขที่มากกว่า 0</p>';
            return;
        }

        // --- Calculations ---
        const proteinCalories = proteinGrams * 4;
        const fatCalories = fatGrams * 9;
        const remainingCalories = tdee - proteinCalories - fatCalories;
        
        if (remainingCalories < 0) {
             resultDiv.innerHTML = '<p class="error">แคลอรี่จากโปรตีนและไขมันสูงกว่า TDEE ของคุณ ไม่สามารถคำนวณคาร์โบไฮเดรตได้</p>';
            return;
        }
        
        const carbGrams = remainingCalories / 4;

        // --- Display result ---
        resultDiv.innerHTML = `
            <h3 style="text-align:center; margin-top: 30px;">สารอาหารสำหรับวันรีฟีดของคุณ</h3>
            <div class="result-table">
                <div class="result-row">
                    <div class="result-label"><p>โปรตีน</p></div>
                    <div class="result-value-box loss">
                        <p class="main-calories">${proteinGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                    </div>
                </div>
                 <div class="result-row">
                    <div class="result-label"><p>ไขมัน</p></div>
                    <div class="result-value-box gain">
                        <p class="main-calories">${fatGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                    </div>
                </div>
                <div class="result-row">
                    <div class="result-label" style="background-color: #f0faff;"><p style="font-weight: bold; color: #004085;">คาร์โบไฮเดรต</p><p class="sub-text">จากแคลอรี่ที่เหลือ</p></div>
                    <div class="result-value-box maintain" style="background-color: #e6f7ff;">
                        <p class="main-calories" style="color: #1d39c4; font-size: 2.2em;">${carbGrams.toFixed(0)} <span style="font-size: 0.5em; font-weight: normal;">กรัม</span></p>
                    </div>
                </div>
                <div class="result-row main-result">
                    <div class="result-label"><p>พลังงานรวม</p></div>
                    <div class="result-value-box"><p class="main-calories" style="font-size: 1.8em; color: #333;">${tdee.toFixed(0)}</p><p class="sub-text">แคลอรี่</p></div>
                </div>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateRefeed);
    }
});