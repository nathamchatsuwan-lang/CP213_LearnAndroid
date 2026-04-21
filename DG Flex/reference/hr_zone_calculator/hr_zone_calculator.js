// --- Heart Rate Zone Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-hr-btn');

    const zones = [
        { name: "โซน 1", percent: "50-60%", purpose: "ฟื้นฟูร่างกาย, เบาสบาย", color: "#a0d911" },
        { name: "โซน 2", percent: "60-70%", purpose: "เผาผลาญไขมัน, ความทนทานพื้นฐาน", color: "#1890ff" },
        { name: "โซน 3", percent: "70-80%", purpose: "พัฒนาสมรรถภาพแอโรบิก (คาร์ดิโอ)", color: "#52c41a" },
        { name: "โซน 4", percent: "80-90%", purpose: "เพิ่มประสิทธิภาพ, ขีดจำกัดแอนแอโรบิก", color: "#fadb14" },
        { name: "โซน 5", percent: "90-100%", purpose: "ความพยายามสูงสุด, พัฒนาความเร็ว", color: "#f5222d" }
    ];

    function calculateHRZones() {
        const resultDiv = document.getElementById('hr-result');
        const age = parseInt(document.getElementById('hr-age').value);

        // --- Validation ---
        if (isNaN(age) || age <= 0 || age > 120) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกอายุให้ถูกต้อง</p>';
            return;
        }

        // --- HRmax Calculation (Tanaka formula is primary) ---
        const hrMaxTanaka = 208 - (0.7 * age);
        const hrMaxFox = 220 - age;
        
        // --- Generate Zone Table ---
        let tableHTML = `
            <div class="bmi-result-container" style="margin-top: 30px; text-align: center;">
                <p>อัตราการเต้นหัวใจสูงสุด (HRmax) ของคุณคือ:</p>
                <p class="bmi-value normal">${Math.round(hrMaxTanaka)}</p>
                <p class="bmi-category normal">ครั้งต่อนาที (bpm)</p>
                <p class="sub-text" style="color: #666; margin-top: 10px;">(คำนวณด้วยสูตร Tanaka, ค่าจากสูตร 220-อายุ คือ ${Math.round(hrMaxFox)} bpm)</p>
            </div>
            <div class="result-table zone-table" style="margin-top: 20px;">
        `;

        for (const zone of zones) {
            const [lowPercent, highPercent] = zone.percent.replace(/%/g, '').split('-').map(Number);
            const lowBPM = hrMaxTanaka * (lowPercent / 100);
            const highBPM = hrMaxTanaka * (highPercent / 100);

            tableHTML += `
                 <div class="result-row">
                    <div class="result-label">
                        <p><span class="zone-color-box" style="background-color: ${zone.color};"></span>${zone.name}</p>
                        <p class="sub-text">${zone.percent}</p>
                    </div>
                    <div class="result-value-box" style="border-left: 3px solid ${zone.color};">
                        <p style="font-weight: bold; color: #333;">${Math.round(lowBPM)} - ${Math.round(highBPM)} bpm</p>
                        <p class="sub-text">${zone.purpose}</p>
                    </div>
                </div>
            `;
        }

        tableHTML += '</div>';
        resultDiv.innerHTML = tableHTML;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateHRZones);
    }
});