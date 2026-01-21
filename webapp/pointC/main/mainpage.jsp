<%@page import="java.util.List"%>
<%@page import="pointC.todo.TodoDTO"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="java.util.Map" %>
<%@ page import="pointC.log.LogDTO" %> <%-- LogDTO 패키지명 확인해주세요! --%>
<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POINT:C - 메인</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/pointC/css/main.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>

<header class="topbar">
  <div class="brand">
    POINT:C
    <%
        // 세션에서 이름 꺼내기
        String userName = (String) session.getAttribute("userName");
        if (userName == null) userName = "사용자";
    %>
    <span class="user-msg">
        반가워요, <strong><%= userName %></strong>님! 👋
    </span>
  </div>

  <div class="top-actions">
    <button class="ghost-btn" onclick="location.href='<%=request.getContextPath()%>/mypage'">마이페이지</button>
    <button class="ghost-btn" onclick="location.href='<%=request.getContextPath()%>/logout.do'">로그아웃</button>
  </div>
</header>

<main class="dash">

  <section class="card card-graph">
    <div class="card-head">
      <h2>📈 분석 결과 그래프</h2>
      <div class="head-actions">
        <button class="mini-btn" type="button" onclick="location.href='<%=request.getContextPath()%>/analysisPage.do'">
    		자세 분석하기
		</button>
      </div>
    </div>

    <div class="card-body" style="display: flex; flex-direction: column; gap: 20px;">
        
        <div class="chart-container" style="position: relative; height: 200px; width: 100%;">
            <canvas id="postureChart"></canvas>
        </div>

        <div class="stats-container" style="display: flex; gap: 15px;">
            
            <%
                // 데이터 유무 확인 및 값 처리
                Boolean hasDataObj = (Boolean) request.getAttribute("hasData");
                boolean hasData = (hasDataObj != null) && hasDataObj.booleanValue();
                Object scoreObj = request.getAttribute("todayScore");
                String badPoint = (String) request.getAttribute("badPoint");
                if(badPoint == null) badPoint = "-"; // 데이터 없으면 대시(-) 표시
            %>

            <div class="stat-box" style="flex: 1; border: 1px solid #eee; border-radius: 12px; padding: 20px; background: #fff;">
                <div style="font-size: 14px; color: #888; margin-bottom: 8px;">오늘 자세 점수</div>
                <div style="font-size: 28px; font-weight: bold; color: #4a6cf7;">
                    <%= hasData ? scoreObj + "점" : "데이터 없음" %>
                </div>
            </div>

            <div class="stat-box" style="flex: 1; border: 1px solid #eee; border-radius: 12px; padding: 20px; background: #fff;">
                <div style="font-size: 14px; color: #888; margin-bottom: 8px;">주의할 부위</div>
                <div style="font-size: 28px; font-weight: bold; color: #fa5252;">
                    <%= hasData ? badPoint : "-" %>
                </div>
            </div>

        </div>
    </div>
  </section>

  <section class="card card-hw">
    <div class="card-head">
      <h2>⚙️ 장치 연결 관리</h2>
    </div>

    <div class="card-body">
      <div class="hw-row">
        <div class="hw-left">
          <div class="label">상태</div>
          <div id="ble-status" class="value" style="color:#fa5252;">미연결</div>
        </div>
        
        <div style="text-align:right;">
             <div class="label">유지 시간</div>
             <div class="value" style="font-size:20px;">
                <span id="ble-hold">0</span>초
             </div>
        </div>
      </div>

      <button id="btn-connect" class="primary-btn" type="button" onclick="connectBluetooth()">
          기기 연결
      </button>

      <p id="ble-msg" class="muted" style="margin-top:15px;">
        기기 연결 버튼을 눌러<br>PostureMonitor를 찾아주세요.
      </p>
      
      <div style="font-size:11px; color:#aaa; margin-top:5px; text-align:right;">
        Pitch: <span id="val-pitch">0</span> | Roll: <span id="val-roll">0</span>
      </div>
    </div>
  </section>

  <section class="card card-plan">
    <div class="card-head">
      <h2>🗓️ 캘린더</h2>
      <div class="head-actions">
        <button class="mini-btn" type="button">캘린더</button>
        <button class="mini-btn" type="button">투두</button>
      </div>
    </div>

    <div class="card-body plan-body">
      <div class="calendar-area">
        <%
            String selectedDate = (String) request.getAttribute("selectedDate");
            LocalDate currentDateObj = (LocalDate) request.getAttribute("currentDateObj");
            Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("calendarStats");
            
            // 안전장치
            if (currentDateObj == null) currentDateObj = LocalDate.now();
            if (stats == null) stats = new java.util.HashMap<>();
            if (selectedDate == null) selectedDate = currentDateObj.toString();
            
            YearMonth ym = YearMonth.from(currentDateObj);
            LocalDate firstDayOfMonth = ym.atDay(1);
            int daysInMonth = ym.lengthOfMonth();
            int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        %>
        
        <div class="graph-title">
            <%= currentDateObj.getYear() %>년 <%= currentDateObj.getMonthValue() %>월
        </div>
        
        <div class="calendar-grid">
            <div class="cal-header" style="color:red">일</div>
            <div class="cal-header">월</div>
            <div class="cal-header">화</div>
            <div class="cal-header">수</div>
            <div class="cal-header">목</div>
            <div class="cal-header">금</div>
            <div class="cal-header" style="color:blue">토</div>

            <% for(int i=0; i<startDayOfWeek; i++) { %>
                <div class="cal-empty"></div>
            <% } %>

            <% for(int d=1; d<=daysInMonth; d++) { 
                String dateStr = String.format("%04d-%02d-%02d", currentDateObj.getYear(), currentDateObj.getMonthValue(), d);
                int level = stats.getOrDefault(dateStr, 0);
                String activeClass = dateStr.equals(selectedDate) ? "active" : "";
            %>
                <a href="<%=request.getContextPath()%>/main.do?date=<%=dateStr%>" 
                   class="cal-day level-<%=level%> <%=activeClass%>">
                   <%= d %>
                </a>
            <% } %>
        </div>
        
        <div class="graph-desc" style="margin-top:10px; font-size:11px; text-align:right;">
            <span style="color:#339af0">■</span>완료 
            <span style="color:#a5d8ff">■</span>진행중
        </div>
      </div>

      <div class="todo-area">
        <div class="todo-title">To-Do</div>

        <form action="<%=request.getContextPath()%>/todo.do" method="post" style="margin-bottom:10px; display:flex; gap:5px;">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="date" value="<%=selectedDate%>"> 
            
            <input type="text" name="content" placeholder="할 일을 입력하세요" required 
                   style="flex:1; padding:5px; border:1px solid #ddd; border-radius:4px;">
            <button type="submit" class="mini-btn" style="background:#555; color:#fff;">+</button>
        </form>

        <div class="todo-list-wrap" style="max-height:150px; overflow-y:auto;">
        <% 
           List<TodoDTO> list = (List<TodoDTO>) request.getAttribute("todoList");
           
           if (list != null && !list.isEmpty()) {
               for (TodoDTO dto : list) {
                   boolean isDone = "1".equals(dto.getIsCompleted());
                   String checked = isDone ? "checked" : "";
                   String textStyle = isDone ? "text-decoration:line-through; color:#999;" : "color:#333;";
        %>
            <div class="todo-item" style="display:flex; align-items:center; justify-content:space-between; padding:8px 10px;">
              <form action="<%=request.getContextPath()%>/todo.do" method="post" style="margin:0; display:flex; align-items:center; flex:1;">
                  <input type="hidden" name="action" value="toggle">
                  <input type="hidden" name="idx" value="<%=dto.getIdx()%>">
                  <input type="hidden" name="date" value="<%=selectedDate%>">
                  <input type="checkbox" <%= checked %> onchange="this.form.submit()" style="cursor:pointer; margin-right:10px; width:18px; height:18px;">
                  <span style="<%= textStyle %>; font-size:14px; font-weight:bold;"><%= dto.getContent() %></span>
              </form>
              <a href="<%=request.getContextPath()%>/todo.do?action=delete&idx=<%=dto.getIdx()%>&date=<%=selectedDate%>" 
                 class="del-btn" onclick="return confirm('정말 삭제하시겠습니까?');"
                 style="text-decoration:none; display:flex; align-items:center; justify-content:center;">✕</a>
            </div>
        <% 
               }
           } else { 
        %>
            <div style="padding:20px; color:#888; font-size:14px; text-align:center;">
                등록된 일정이 없습니다.<br><small>위 입력창에 할 일을 적어보세요!</small>
            </div>
        <% } %>
        </div>
      </div>
    </div>
  </section>

  <section class="card card-log">
    <div class="card-head">
      <h2>🛎️ 알림 기록</h2>
    </div>

    <div class="card-body log-body">
	<%
	  List<pointC.log.LogDTO> logs = (List<pointC.log.LogDTO>) request.getAttribute("logList");
	  
	  if(logs != null && !logs.isEmpty()) {
	      for(pointC.log.LogDTO log : logs) {
	          String color = "WARN".equals(log.getType()) ? "color:#fa5252;" : "color:#333;";
	          
	          // [수정] Date 타입을 예쁜 문자열로 변환 (예: 10:25)
	          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
	          String timeStr = sdf.format(log.getRegdate());
	%>
	<div class="log-item">
	  <div class="log-time"><%= timeStr %></div>
	  <div class="log-text" style="<%= color %>">
	     <%= log.getMessage() %> 
	  </div>
	</div>
	<% 
	      }
	  } else {
	%>
	<div style="text-align:center; color:#999; padding:20px;">기록이 없습니다.</div>
	<% } %>
	</div>
  </section>

</main>

<script>
  // 1. 차트 설정 (기존 유지)
  const ctx = document.getElementById('postureChart').getContext('2d');
  
  // 데이터 안전 처리
  const labelsData = [<%= request.getAttribute("graphLabels") != null ? request.getAttribute("graphLabels") : "" %>];
  const scoreData = [<%= request.getAttribute("graphData") != null ? request.getAttribute("graphData") : "" %>];

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: labelsData, 
      datasets: [{
        label: '자세 점수',
        data: scoreData, 
        borderColor: '#4a6cf7',
        backgroundColor: 'rgba(74, 108, 247, 0.1)',
        tension: 0.4,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, max: 100, ticks: { stepSize: 20 } } }
    }
  });

  // ===== [2. 블루투스 + 알림 + DB저장] 핵심 로직 =====

  const SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
  const CHAR_UUID    = "beb5483e-36e1-4688-b7f5-ea07361b26a8";

  const STRETCH_THRESHOLD = 10; // 5분 (테스트할 땐 10 정도로 줄여보세요)
  const BAD_POSTURE_ANGLE = 50;  // 15도 이상 꺾이면 경고

  let bleDevice = null;
  let isAlertSent = false; // 알림 중복 방지 깃발

  // ★ [핵심] 서버에 알림 기록 저장하는 함수 (AJAX)
  function saveLogToServer(msg, type) {
      // 현재 로그인한 사용자 ID 가져오기 (세션값)
      const userId = "<%= session.getAttribute("userId") %>"; 
      
      // /saveLog.do 컨트롤러 호출 (화면 깜빡임 없이 백그라운드 전송)
      fetch('<%=request.getContextPath()%>/saveLog.do', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
          },
          // 데이터 전송: userId=sua&message=거북목...&type=WARN
          body: 'userId=' + userId + '&message=' + encodeURIComponent(msg) + '&type=' + type
      }).then(response => {
          console.log("DB 기록 저장 완료:", msg);
      }).catch(error => {
          console.error("DB 저장 실패:", error);
      });
  }

  async function connectBluetooth() {
      try {
          if (Notification.permission !== "granted") {
              await Notification.requestPermission();
          }
          const device = await navigator.bluetooth.requestDevice({
              filters: [{ name: 'PostureMonitor' }],
              optionalServices: [SERVICE_UUID]
          });
          bleDevice = device;
          device.addEventListener('gattserverdisconnected', onDisconnected);
          const server = await device.gatt.connect();
          const service = await server.getPrimaryService(SERVICE_UUID);
          const characteristic = await service.getCharacteristic(CHAR_UUID);
          await characteristic.startNotifications();
          characteristic.addEventListener('characteristicvaluechanged', handleValueChanged);
          updateUI(true);
      } catch (error) {
          console.log(error);
      }
  }

  function handleValueChanged(event) {
      const value = event.target.value;
      const jsonStr = new TextDecoder('utf-8').decode(value);
      
      try {
          const data = JSON.parse(jsonStr); 
          
          // 화면 숫자 업데이트
          document.getElementById('ble-hold').innerText = data.hold;
          document.getElementById('val-pitch').innerText = data.pitch;
          document.getElementById('val-roll').innerText = data.roll;
          
          const msgBox = document.getElementById('ble-msg');
          const statusDiv = document.getElementById('ble-status');

          // ========================================================
          // [조건 1] 5분 이상 부동 자세일 때 (스트레칭 알림)
          // ========================================================
          if (data.hold >= STRETCH_THRESHOLD) {
              msgBox.innerText = "5분이 지났습니다! 스트레칭하세요!";
              statusDiv.innerText = "스트레칭 필요";
              statusDiv.style.color = "#fd7e14";

              // 알림을 아직 안 보냈다면? -> 보낸다!
              if (!isAlertSent && Notification.permission === "granted") {
                  
                  // 1. 웹 푸시 띄우기
                  new Notification("🧘 스트레칭 시간!", {
                      body: "5분 동안 움직임이 없어요. 기지개를 켜보세요!",
                      icon: "<%=request.getContextPath()%>/pointC/images/logo.png"
                  });

                  // 2. ★ DB에 기록 남기기 (이게 핵심!)
                  saveLogToServer("5분 부동자세 감지 (스트레칭 알림)", "INFO");

                  isAlertSent = true; // 깃발 꽂기 (도배 방지)
              }
          } 
          // ========================================================
          // [조건 2] 자세가 15도 이상 꺾였을 때 (거북목 경고)
          // ========================================================
          else if (Math.abs(data.pitch) > BAD_POSTURE_ANGLE) {
              msgBox.innerText = "고개가 너무 꺾였습니다!";
              statusDiv.innerText = "자세 경고";
              statusDiv.style.color = "red";

              if (!isAlertSent && Notification.permission === "granted") {
                  // 1. 웹 푸시
                  new Notification("⚠️ 자세 경고", {
                      body: "거북목이 감지되었습니다. 고개를 드세요!",
                  });

                  // 2. ★ DB 기록
                  saveLogToServer("거북목 자세 감지 (경고)", "WARN");

                  isAlertSent = true;
              }
          }
          // ========================================================
          // [조건 3] 정상 상태 (복구)
          // ========================================================
          else {
              msgBox.innerText = "실시간 모니터링 중...";
              statusDiv.innerText = "정상";
              statusDiv.style.color = "#28a745";
              
              // 자세가 돌아오거나 시간이 초기화되면 알림 깃발 내리기
              // (그래야 다음에 또 알림을 보낼 수 있음)
              if (data.hold < 5 && Math.abs(data.pitch) < BAD_POSTURE_ANGLE) {
                  isAlertSent = false;
              }
          }
          
      } catch (e) {}
  }

  function onDisconnected() {
      bleDevice = null;
      updateUI(false);
  }
  function updateUI(isConnected) {
      const btn = document.getElementById('btn-connect');
      const statusDiv = document.getElementById('ble-status');
      if (isConnected) {
          statusDiv.innerText = "연결됨";
          statusDiv.style.color = "green";
          btn.innerText = "연결 해제";
          btn.onclick = () => { if(bleDevice) bleDevice.gatt.disconnect(); };
      } else {
          statusDiv.innerText = "미연결";
          statusDiv.style.color = "red";
          btn.innerText = "기기 연결";
          btn.onclick = connectBluetooth;
      }
  }
</script>
</body>
</html>