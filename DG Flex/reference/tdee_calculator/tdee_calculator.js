// [แก้ไข] นำเข้าเครื่องมือที่จำเป็นจาก Firebase และ Modal ใหม่
import { auth, db } from '../../asset/js/firebase-config.js';
import { showAppModal } from '../../asset/js/main.js'; // <-- เพิ่มบรรทัดนี้
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, setDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

let currentUser = null;
const resultDiv = document.getElementById('result');
const saveResultContainer = document.getElementById('save-result-container');

onAuthStateChanged(auth, (user) => {
    currentUser = user;
    saveResultContainer.innerHTML = '';
    resultDiv.innerHTML = '';
});

function calculateCalories() {
  const age = parseInt(document.getElementById('age').value);
  const gender = document.querySelector('input[name="gender"]:checked');
  const weight = parseFloat(document.getElementById('weight').value);
  const height = parseFloat(document.getElementById('height').value);
  const activityLevel = parseFloat(document.getElementById('activity-level').value);
  
  saveResultContainer.innerHTML = ''; 

  if (isNaN(age) || !gender || isNaN(weight) || isNaN(height) || !activityLevel) {
    resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
    return;
  }
  if (age <= 0 || weight <= 0 || height <= 0) {
    resultDiv.innerHTML = '<p class="error">กรุณากรอก อายุ, น้ำหนัก, และส่วนสูงเป็นตัวเลขที่มากกว่า 0</p>';
    return;
  }
  
  let bmr;
  if (gender.value === 'male') {
    bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
  } else {
    bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
  }

  if (activityLevel === 1.0) {
    displayBmrOnly(bmr);
    if (currentUser) {
        showSaveButton({ bmr: Math.round(bmr), tdee: null });
    }
  } else {
    const maintenanceCalories = bmr * activityLevel;
    displayTdeeResults(maintenanceCalories);
    if (currentUser) {
        showSaveButton({ bmr: Math.round(bmr), tdee: Math.round(maintenanceCalories) });
    }
  }
}

function showSaveButton(tdeeData) {
    saveResultContainer.innerHTML = `<button id="save-tdee-btn" class="btn-primary" style="background-color: #28a745;">บันทึกผลลัพธ์นี้</button>`;
    
    document.getElementById('save-tdee-btn').addEventListener('click', async () => {
        await saveTdeeToFirestore(tdeeData);
    });
}

// [แก้ไข] ฟังก์ชันสำหรับบันทึกข้อมูลลง Firestore ให้ใช้ Modal
async function saveTdeeToFirestore(data) {
    if (!currentUser) {
        showAppModal("ต้องเข้าสู่ระบบ", "กรุณาล็อกอินก่อนบันทึกข้อมูล", "info");
        return;
    }
    try {
        const userDocRef = doc(db, "users", currentUser.uid);
        await setDoc(userDocRef, { tdeeHistory: data }, { merge: true });
        showAppModal("บันทึกสำเร็จ!", "บันทึกผล TDEE ของคุณเรียบร้อยแล้ว", "success");
    } catch (e) {
        console.error("เกิดข้อผิดพลาดในการบันทึกข้อมูล: ", e);
        showAppModal("เกิดข้อผิดพลาด", "ไม่สามารถบันทึกข้อมูลได้ กรุณาลองใหม่อีกครั้ง", "error");
    }
}

// --- โค้ดส่วนแสดงผลเดิม ---
function displayBmrOnly(bmrValue) {
    resultDiv.innerHTML = `
        <div class="bmr-result-box">
            <div class="result-label"><p>อัตราการเผาผลาญพื้นฐาน (BMR)</p></div>
            <div class="result-value-box maintain">
                <p class="main-calories">${Math.round(bmrValue)}</p>
                <p class="sub-text">แคลอรี่/วัน</p>
            </div>
        </div>
    `;
}

function displayTdeeResults(caloriesToMaintain) {
  const maintain = caloriesToMaintain;
  const mildLoss = maintain - 250;
  const normalLoss = maintain - 500;
  const extremeLoss = maintain - 1000;
  const mildGain = maintain + 250;
  const normalGain = maintain + 500;
  const fastGain = maintain + 1000;

  resultDiv.innerHTML = `
    <div class="result-accordion">
        <div class="result-row main-result">
            <div class="result-label"><p>รักษาน้ำหนัก (TDEE)</p></div>
            <div class="result-value-box maintain"><p class="main-calories">${Math.round(maintain)}</p><p class="sub-text">แคลอรี่/วัน</p></div>
        </div>
        <div class="result-toggle" id="toggle-loss">เป้าหมาย: ลดน้ำหนัก <span class="arrow">▶</span></div>
        <div class="collapsible-content" id="content-loss">
             <div class="result-row"><div class="result-label"><p>ลดน้ำหนักเล็กน้อย</p><p class="sub-text">0.25 กก./สัปดาห์</p></div><div class="result-value-box loss"><p class="main-calories">${Math.round(mildLoss)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
            <div class="result-row"><div class="result-label"><p>ลดน้ำหนัก</p><p class="sub-text">0.5 กก./สัปดาห์</p></div><div class="result-value-box loss"><p class="main-calories">${Math.round(normalLoss)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
            <div class="result-row"><div class="result-label"><p>ลดน้ำหนักเร่งด่วน</p><p class="sub-text">1 กก./สัปดาห์</p></div><div class="result-value-box loss"><p class="main-calories">${Math.round(extremeLoss)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
        </div>
        <div class="result-toggle" id="toggle-gain">เป้าหมาย: เพิ่มน้ำหนัก <span class="arrow">▶</span></div>
        <div class="collapsible-content" id="content-gain">
            <div class="result-row"><div class="result-label"><p>เพิ่มน้ำหนักเล็กน้อย</p><p class="sub-text">0.25 กก./สัปดาห์</p></div><div class="result-value-box gain"><p class="main-calories">${Math.round(mildGain)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
            <div class="result-row"><div class="result-label"><p>เพิ่มน้ำหนัก</p><p class="sub-text">0.5 กก./สัปดาห์</p></div><div class="result-value-box gain"><p class="main-calories">${Math.round(normalGain)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
            <div class="result-row"><div class="result-label"><p>เพิ่มน้ำหนักเร่งด่วน</p><p class="sub-text">1 กก./สัปดาห์</p></div><div class="result-value-box gain"><p class="main-calories">${Math.round(fastGain)}</p><p class="sub-text">แคลอรี่/วัน</p></div></div>
        </div>
    </div>
  `;
  addToggleListeners();
}

function addToggleListeners() {
    const lossToggle = document.getElementById('toggle-loss');
    const gainToggle = document.getElementById('toggle-gain');
    if (lossToggle) {
        lossToggle.addEventListener('click', function() {
            this.classList.toggle('active');
            this.querySelector('.arrow').textContent = this.classList.contains('active') ? '▲' : '▼';
            document.getElementById('content-loss').classList.toggle('active');
        });
    }
    if (gainToggle) {
        gainToggle.addEventListener('click', function() {
            this.classList.toggle('active');
            this.querySelector('.arrow').textContent = this.classList.contains('active') ? '▲' : '▼';
            document.getElementById('content-gain').classList.toggle('active');
        });
    }
}

document.getElementById('calculate-btn').addEventListener('click', calculateCalories);