// --- Waist-Hip Ratio (WHR) Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-whr-btn');

    // Function to get WHR category and class based on WHO standards
    function getWhrCategory(whr, gender) {
        if (gender === 'male') {
            if (whr < 0.90) return { category: 'ความเสี่ยงต่อสุขภาพต่ำ', className: 'normal' };
            return { category: 'ความเสี่ยงต่อสุขภาพสูง (ภาวะอ้วนลงพุง)', className: 'obese' };
        } else { // female
            if (whr < 0.85) return { category: 'ความเสี่ยงต่อสุขภาพต่ำ', className: 'normal' };
            return { category: 'ความเสี่ยงต่อสุขภาพสูง (ภาวะอ้วนลงพุง)', className: 'obese' };
        }
    }
    
    // Main function to calculate WHR
    function calculateWHR() {
        const gender = document.querySelector('input[name="gender"]:checked').value;
        const units = document.getElementById('units').value;
        const resultDiv = document.getElementById('whr-result');

        let waist = parseFloat(document.getElementById('whr-waist').value);
        let hip = parseFloat(document.getElementById('whr-hip').value);

        // --- Basic Validation ---
        if (isNaN(waist) || isNaN(hip)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลรอบเอวและสะโพกให้ครบถ้วน</p>';
            return;
        }

        if (waist <= 0 || hip <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขที่มากกว่า 0</p>';
            return;
        }

        // --- Unit Conversion ---
        if (units === 'in') {
            waist *= 2.54;
            hip *= 2.54;
        }

        // --- Calculation ---
        const whr = waist / hip;

        if (!isFinite(whr)) {
             resultDiv.innerHTML = '<p class="error">ไม่สามารถคำนวณได้ กรุณาตรวจสอบค่าที่กรอก</p>';
            return;
        }
        
        const roundedWHR = whr.toFixed(2);
        const { category, className } = getWhrCategory(roundedWHR, gender);

        // --- Display result ---
        resultDiv.innerHTML = `
            <div class="bmi-result-display">
                <p>ค่า WHR ของคุณคือ:</p>
                <p class="bmi-value ${className}">${roundedWHR}</p>
                <p class="bmi-category ${className}">ผลลัพธ์: ${category}</p>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateWHR);
    }
});