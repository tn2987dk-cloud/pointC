<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POINT:C - AI 자세 분석</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/pointC/css/main.css">
<style>
    /* 업로드 박스 전용 스타일 (여기에만 쓸 거라 내부에 둠) */
    .upload-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr); /* 4열 배치 */
        gap: 15px;
        margin: 20px 0;
    }
    .upload-box {
        border: 2px dashed #d6dae3;
        border-radius: 12px;
        height: 200px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        background: #fafbfc;
        transition: 0.2s;
        position: relative;
        overflow: hidden;
    }
    .upload-box:hover { border-color: #4a6cf7; background: #f0f4ff; }
    
    .box-label { font-weight: bold; color: #555; margin-bottom: 8px; }
    .box-desc { font-size: 12px; color: #888; }
    
    /* 실제 파일 input은 숨김 */
    .file-input { display: none; }
    
    /* 이미지 미리보기 */
    .preview-img {
        width: 100%; height: 100%;
        object-fit: cover;
        position: absolute; top: 0; left: 0;
        display: none; /* 처음엔 숨김 */
    }

    /* 로딩 오버레이 */
    .loading-overlay {
        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(255,255,255,0.9);
        z-index: 999;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        display: none; /* 평소엔 숨김 */
    }
    .loader {
        border: 5px solid #f3f3f3;
        border-top: 5px solid #4a6cf7;
        border-radius: 50%;
        width: 50px; height: 50px;
        animation: spin 1s linear infinite;
    }
    @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
    
    /* 반응형: 모바일은 2줄로 */
    @media (max-width: 768px) {
        .upload-grid { grid-template-columns: repeat(2, 1fr); }
    }
    
    .alert-box {
        background-color: #fff3cd; /* 연한 노란색 배경 */
        color: #856404;            /* 진한 갈색 글자 */
        border: 1px solid #ffeeba;
        padding: 16px;
        border-radius: 8px;
        margin-bottom: 20px;       /* 아래 업로드 박스와 간격 */
        font-size: 14px;
        line-height: 1.5;
        display: flex;             /* 아이콘과 글자 정렬 */
        align-items: start;
        gap: 10px;
    }
    .alert-icon {
        font-size: 20px;
    }
</style>
</head>
<body>

    <div class="topbar">
        <div class="brand">POINT:C <span class="user-msg">AI 분석 센터</span></div>
        <div class="top-actions">
            <button class="ghost-btn" onclick="location.href='main.do'">메인으로</button>
        </div>
    </div>

    <div class="dash" style="display: block;"> <section class="card">
            <div class="card-head">
                <h2>사진 업로드</h2>
            </div>
            <div class="card-body">
            
            	<div class="alert-box">
                    <span class="alert-icon">⚠️</span>
                    <div>
                        <strong>주의사항:</strong><br>
                        본 서비스의 AI 분석 결과는 단순 참고용이며, <b>의학적 진단이 아닙니다.</b><br>
                        정확한 건강 상태 확인 및 진단은 반드시 전문 의료기관을 방문하여 의사와 상담하시기 바랍니다.
                    </div>
                </div>
                
                <div style="text-align: center; color: #666; margin-bottom: 20px;">
                    정확한 분석을 위해 <b>전신이 나오도록</b> 4방향 사진을 업로드해주세요.<br>
                </div>

                <form action="<%=request.getContextPath()%>/analyze.do" method="post" enctype="multipart/form-data" onsubmit="showLoading()">
                    
                    <div class="upload-grid">
                        <label class="upload-box">
                            <span class="box-label">앞모습 (Front)</span>
                            <span class="box-desc">클릭하여 업로드</span>
                            <img id="prev-front" class="preview-img">
                            <input type="file" name="front" class="file-input" accept="image/*" onchange="preview(this, 'prev-front')">
                        </label>

                        <label class="upload-box">
                            <span class="box-label">뒷모습 (Back)</span>
                            <span class="box-desc">클릭하여 업로드</span>
                            <img id="prev-back" class="preview-img">
                            <input type="file" name="back" class="file-input" accept="image/*" onchange="preview(this, 'prev-back')">
                        </label>

                        <label class="upload-box">
                            <span class="box-label">왼쪽 (Left)</span>
                            <span class="box-desc">귀와 어깨가 보이게</span>
                            <img id="prev-left" class="preview-img">
                            <input type="file" name="left" class="file-input" accept="image/*" onchange="preview(this, 'prev-left')">
                        </label>

                        <label class="upload-box">
                            <span class="box-label">오른쪽 (Right)</span>
                            <span class="box-desc">귀와 어깨가 보이게</span>
                            <img id="prev-right" class="preview-img">
                            <input type="file" name="right" class="file-input" accept="image/*" onchange="preview(this, 'prev-right')">
                        </label>
                    </div>

                    <button type="submit" class="primary-btn" style="font-size: 18px;">
                        AI 분석 시작하기
                    </button>
                </form>
            </div>
        </section>
        
       <section class="card" style="margin-top: 20px;">
       <div class="card-head">
           <h2>📜 지난 분석 기록</h2>
       </div>
       <div class="card-body">
           
           <%
               java.util.List<pointC.analysis.AnalysisDTO> list = 
                   (java.util.List<pointC.analysis.AnalysisDTO>) request.getAttribute("historyList");
               
               if (list != null && !list.isEmpty()) {
           %>
           <table style="width: 100%; border-collapse: collapse; text-align: left;">
               <tr style="border-bottom: 2px solid #eee; color: #888;">
                   <th style="padding: 10px;">날짜</th>
                   <th style="padding: 10px;">점수</th>
                   <th style="padding: 10px;">취약 부위</th>
                   <th style="padding: 10px;">피드백</th>
               </tr>
               <% for(pointC.analysis.AnalysisDTO dto : list) { %>
               <tr style="border-bottom: 1px solid #f0f0f0;">
                   <td style="padding: 12px 10px;"><%= dto.getRegdate() %></td>
                   <td style="padding: 12px 10px; font-weight: bold; color: #4a6cf7;">
                       <%= dto.getScore() %>점
                   </td>
                   <td style="padding: 12px 10px; color: #fa5252;">
                       <%= dto.getBadParts() %>
                   </td>
                   <td style="padding: 12px 10px; font-size: 13px; color: #555;">
                       <%= dto.getFeedback() %>
                   </td>
               </tr>
               <% } %>
           </table>
           <% } else { %>
               <div style="text-align: center; padding: 30px; color: #999;">
                   아직 분석 기록이 없습니다. <br>
                   위 버튼을 눌러 첫 분석을 시작해보세요!
               </div>
           <% } %>
            
        	</div>
    	</section>
	</div>

    </div>

    <div id="loadingScreen" class="loading-overlay">
        <div class="loader"></div>
        <h3 style="margin-top: 20px; color: #333;">AI가 자세를 분석 중입니다...</h3>
        <p style="color: #666;">잠시만 기다려주세요 (최대 10초)</p>
    </div>

<script>
    // 이미지 미리보기 함수
    function preview(input, imgId) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var img = document.getElementById(imgId);
                img.src = e.target.result;
                img.style.display = "block"; // 이미지 보이기
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

 	// 이미지 미리보기 함수 (기존 유지)
    function preview(input, imgId) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var img = document.getElementById(imgId);
                img.src = e.target.result;
                img.style.display = "block"; 
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    // [수정] 유효성 검사 + 로딩창 제어 함수
    function validateForm() {
        // 1. input 태그들 가져오기 (name 속성으로 찾음)
        const front = document.getElementsByName('front')[0];
        const back = document.getElementsByName('back')[0];
        const left = document.getElementsByName('left')[0];
        const right = document.getElementsByName('right')[0];

        // 2. 하나라도 비어있으면? -> 경고 & 전송 취소(false)
        if (!front.value || !back.value || !left.value || !right.value) {
            alert("⚠️ 4방향 사진을 모두 업로드해주세요!");
            return false; // 전송 막기! 로딩창 안 뜸!
        }

        // 3. 다 들어있으면? -> 로딩창 띄우고 전송 허용(true)
        document.getElementById('loadingScreen').style.display = 'flex';
        return true; 
    }
</script>

</body>
</html>