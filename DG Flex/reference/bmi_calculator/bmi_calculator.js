// [แก้ไข] นำเข้าเครื่องมือที่จำเป็นจาก Firebase และ Modal ใหม่
import { auth, db } from '../../asset/js/firebase-config.js';
import { showAppModal } from '../../asset/js/main.js'; // <-- เพิ่มบรรทัดนี้
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, setDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

let currentUser = null;

const weightInput = document.getElementById('bmi-weight');
const heightInput = document.getElementById('bmi-height');
const resultDiv = document.getElementById('bmi-result');
const calculateBtn = document.getElementById('calculate-bmi-btn');
const saveResultContainer = document.getElementById('save-result-container');

onAuthStateChanged(auth, (user) => {
    if (user) {
        currentUser = user;
    } else {
        currentUser = null;
        saveResultContainer.innerHTML = '';
    }
});

function calculateBMI() {
    const weight = parseFloat(weightInput.value);
    const height = parseFloat(heightInput.value);

    if (isNaN(weight) || isNaN(height) || weight <= 0 || height <= 0) {
        resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ถูกต้อง</p>';
        saveResultContainer.innerHTML = '';
        return;
    }

    const heightInMeters = height / 100;
    const bmi = weight / (heightInMeters * heightInMeters);
    const roundedBMI = bmi.toFixed(1);

    let category = '';
    let categoryClass = '';
    if (roundedBMI < 18.5) {
        category = 'น้ำหนักน้อย / ผอม';
        categoryClass = 'underweight';
    } else if (roundedBMI <= 24.9) {
        category = 'น้ำหนักปกติ / สุขภาพดี';
        categoryClass = 'normal';
    } else if (roundedBMI <= 29.9) {
        category = 'น้ำหนักเกิน / ท้วม';
        categoryClass = 'overweight';
    } else {
        category = 'อ้วน / โรคอ้วน';
        categoryClass = 'obese';
    }

    resultDiv.innerHTML = `
        <div class="bmi-result-display">
            <p>ค่า BMI ของคุณคือ:</p>
            <p class="bmi-value ${categoryClass}">${roundedBMI}</p>
            <p class="bmi-category ${categoryClass}">ผลลัพธ์: ${category}</p>
        </div>
    `;

    if (currentUser) {
        showSaveButton({ weight, height, bmi: roundedBMI, category });
    }
}

function showSaveButton(bmiData) {
    saveResultContainer.innerHTML = `<button id="save-bmi-btn" class="btn-primary" style="background-color: #28a745;">บันทึกผลลัพธ์นี้</button>`;
    
    document.getElementById('save-bmi-btn').addEventListener('click', async () => {
        await saveBmiToFirestore(bmiData);
    });
}

// [แก้ไข] ฟังก์ชันสำหรับบันทึกข้อมูลลง Firestore ให้ใช้ Modal
async function saveBmiToFirestore(data) {
    if (!currentUser) {
        showAppModal("ต้องเข้าสู่ระบบ", "กรุณาล็อกอินก่อนบันทึกข้อมูล", "info");
        return;
    }

    try {
        const userDocRef = doc(db, "users", currentUser.uid);
        await setDoc(userDocRef, { bmiHistory: data }, { merge: true });
        showAppModal("บันทึกสำเร็จ!", "บันทึกผล BMI ของคุณเรียบร้อยแล้ว", "success");
    } catch (e) {
        console.error("เกิดข้อผิดพลาดในการบันทึกข้อมูล: ", e);
        showAppModal("เกิดข้อผิดพลาด", "ไม่สามารถบันทึกข้อมูลได้ กรุณาลองใหม่อีกครั้ง", "error");
    }
}

if (calculateBtn) {
    calculateBtn.addEventListener('click', calculateBMI);
}