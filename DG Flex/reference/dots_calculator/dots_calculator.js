// --- DOTS Score Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-dots-btn');
    
    // Official DOTS Coefficients
    const coeffs = {
        male:   { a: -0.0000010930, b: 0.0007391293, c: -0.1918759221, d: 24.0900756, e: -307.75076 },
        female: { a: -0.0000010706, b: 0.0005158568, c: -0.1126655495, d: 13.6175032, e: -57.96288 }
    };

    // --- NEW: Function to get DOTS rating ---
    function getDotsRating(score) {
        if (score < 200) return 'ผู้เริ่มต้น (Beginner)';
        if (score < 300) return 'ระดับฝึกฝน (Novice)';
        if (score < 400) return 'ระดับกลาง (Intermediate)';
        if (score < 500) return 'ระดับสูง (Advanced)';
        if (score < 600) return 'ระดับประเทศ / แข่งขัน (National / Competitive)';
        return 'ระดับโลก (World Class / Elite)';
    }

    function calculateDots() {
        const resultDiv = document.getElementById('dots-result');
        const gender = document.querySelector('input[name="gender"]:checked').value;
        const unit = document.getElementById('unit-select').value;
        const bodyWeight = parseFloat(document.getElementById('body-weight').value);
        const squat = parseFloat(document.getElementById('squat-weight').value) || 0;
        const bench = parseFloat(document.getElementById('bench-weight').value) || 0;
        const deadlift = parseFloat(document.getElementById('deadlift-weight').value) || 0;

        // --- Validation ---
        if (isNaN(bodyWeight) || bodyWeight <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักตัวให้ถูกต้อง</p>';
            return;
        }

        // --- Unit Conversion to KG ---
        const toKg = (w) => (unit === 'lbs' ? w / 2.20462 : w);
        const bwKg = toKg(bodyWeight);
        const totalLiftKg = toKg(squat + bench + deadlift);
        
        if (totalLiftKg <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักที่ยกได้อย่างน้อย 1 ท่า</p>';
            return;
        }
        
        // --- DOTS Formula Calculation ---
        const c = coeffs[gender];
        const denominator = (c.a * Math.pow(bwKg, 4)) + (c.b * Math.pow(bwKg, 3)) + (c.c * Math.pow(bwKg, 2)) + (c.d * bwKg) + c.e;
        
        if (denominator === 0) {
             resultDiv.innerHTML = '<p class="error">ไม่สามารถคำนวณได้ กรุณาตรวจสอบข้อมูลอีกครั้ง</p>';
             return;
        }
        
        const dotsScore = (totalLiftKg / denominator) * 500;
        
        // --- NEW: Get the rating text ---
        const ratingText = getDotsRating(dotsScore);

        // --- Display result (Now includes the rating) ---
        resultDiv.innerHTML = `
            <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>คะแนน DOTS ของคุณคือ:</p>
                    <p class="bmi-value normal">${dotsScore.toFixed(2)}</p>
                    <p class="bmi-category normal">DOTS Points</p>

                    <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
                    <p>ระดับความแข็งแกร่งของคุณจัดอยู่ในเกณฑ์:</p>
                    <p style="font-size: 1.5em; font-weight: bold; color: #1890ff; margin-top: 10px;">${ratingText}</p>
                </div>
            </div>
        `;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateDots);
    }
});