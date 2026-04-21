// --- Body Fat Percentage (BFP) Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const genderRadios = document.querySelectorAll('input[name="gender"]');
    const hipGroup = document.getElementById('hip-group');
    const calculateBtn = document.getElementById('calculate-bfp-btn');

    // Function to show/hide hip input based on gender
    function toggleHipInput() {
        if (document.getElementById('female').checked) {
            hipGroup.style.display = 'block';
        } else {
            hipGroup.style.display = 'none';
        }
    }

    // Add event listeners to gender radio buttons
    genderRadios.forEach(radio => {
        radio.addEventListener('change', toggleHipInput);
    });

    // Function to get BFP category and class
    function getBfpCategory(bfp, gender) {
        if (gender === 'male') {
            if (bfp < 6) return { category: 'ไขมันจำเป็น', className: 'underweight' };
            if (bfp <= 13) return { category: 'นักกีฬา', className: 'normal' };
            if (bfp <= 17) return { category: 'ฟิต', className: 'normal' };
            if (bfp <= 24) return { category: 'สุขภาพดี/ทั่วไป', className: 'overweight' };
            return { category: 'ภาวะอ้วน', className: 'obese' };
        } else { // female
            if (bfp < 14) return { category: 'ไขมันจำเป็น', className: 'underweight' };
            if (bfp <= 20) return { category: 'นักกีฬา', className: 'normal' };
            if (bfp <= 24) return { category: 'ฟิต', className: 'normal' };
            if (bfp <= 31) return { category: 'สุขภาพดี/ทั่วไป', className: 'overweight' };
            return { category: 'ภาวะอ้วน', className: 'obese' };
        }
    }
    
    // Main function to calculate Body Fat Percentage
    function calculateBFP() {
        const gender = document.querySelector('input[name="gender"]:checked').value;
        const units = document.getElementById('units').value;
        const resultDiv = document.getElementById('bfp-result');

        // Get new weight value
        const weight = parseFloat(document.getElementById('bfp-weight').value);
        let height = parseFloat(document.getElementById('bfp-height').value);
        let neck = parseFloat(document.getElementById('bfp-neck').value);
        let waist = parseFloat(document.getElementById('bfp-waist').value);
        let hip = 0;
        
        // --- Basic Validation ---
        if (isNaN(weight) || isNaN(height) || isNaN(neck) || isNaN(waist)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลน้ำหนัก, ส่วนสูง, คอ, และเอวให้ครบถ้วน</p>';
            return;
        }

        if (gender === 'female') {
            hip = parseFloat(document.getElementById('bfp-hip').value);
            if (isNaN(hip)) {
                resultDiv.innerHTML = '<p class="error">สำหรับเพศหญิง กรุณากรอกข้อมูลสะโพก</p>';
                return;
            }
        }
        
        if (weight <= 0 || height <= 0 || neck <= 0 || waist <= 0 || (gender === 'female' && hip <= 0)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกตัวเลขที่มากกว่า 0</p>';
            return;
        }

        // --- Unit Conversion ---
        if (units === 'in') {
            height *= 2.54;
            neck *= 2.54;
            waist *= 2.54;
            if (gender === 'female') {
                hip *= 2.54;
            }
        }

        // --- Enhanced Validation (Post-Conversion) ---
        if (gender === 'male' && waist <= neck) {
            resultDiv.innerHTML = '<p class="error">รอบเอวต้องมีค่ามากกว่ารอบคอ (สำหรับเพศชาย)</p>';
            return;
        }
        if (gender === 'female' && (waist + hip) <= neck) {
             resultDiv.innerHTML = '<p class="error">ผลรวมรอบเอวและสะโพกต้องมากกว่ารอบคอ (สำหรับเพศหญิง)</p>';
            return;
        }

        // --- Calculation ---
        let bfp = 0;
        if (gender === 'male') {
            bfp = (495 / (1.0324 - 0.19077 * Math.log10(waist - neck) + 0.15456 * Math.log10(height))) - 450;
        } else {
            bfp = (495 / (1.29579 - 0.35004 * Math.log10(waist + hip - neck) + 0.22100 * Math.log10(height))) - 450;
        }
        
        // --- Final Plausibility Check ---
        if (!isFinite(bfp) || bfp < 1 || bfp > 75) {
             resultDiv.innerHTML = '<p class="error">ไม่สามารถคำนวณจากสัดส่วนนี้ได้ กรุณาตรวจสอบว่ากรอกค่าถูกต้องและเลือกหน่วยวัด (ซม./นิ้ว) ถูกต้องแล้ว</p>';
            return;
        }

        // --- NEW: LBM & Fat Mass Calculation ---
        const fatMass = weight * (bfp / 100);
        const lbm = weight - fatMass;

        const roundedBFP = bfp.toFixed(1);
        const { category, className } = getBfpCategory(roundedBFP, gender);

        // --- Display result (Now includes LBM) ---
        resultDiv.innerHTML = `
            <div class="bmi-result-display">
                <p>เปอร์เซ็นต์ไขมันของคุณคือ:</p>
                <p class="bmi-value ${className}">${roundedBFP}%</p>
                <p class="bmi-category ${className}">ผลลัพธ์: ${category}</p>
                
                <hr style="margin: 20px 0; border: none; border-top: 1px solid #e0e0e0;">

                <div style="text-align: left; padding: 0 10px; font-size: 0.95em;">
                    <p style="margin: 8px 0;"><strong>น้ำหนักตัว:</strong> ${weight.toFixed(1)} กก.</p>
                    <p style="margin: 8px 0;"><strong>มวลไขมัน (Fat Mass):</strong> ${fatMass.toFixed(1)} กก.</p>
                    <p style="margin: 8px 0; font-weight: bold; color: #333;"><strong>มวลร่างกายไม่รวมไขมัน (LBM):</strong> ${lbm.toFixed(1)} กก.</p>
                </div>
            </div>
        `;
    }

    // Add event listener to the calculate button
    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateBFP);
    }
});