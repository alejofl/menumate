<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
  <div class="card" style="width: 18rem;">
    <img
      class="card-img restaurant-card-img"
      style="--main_image: url(${param.main_image}); --hover_image: url(${param.hover_image})"
    >
    <div class="card-body">
      <h5 class="card-title">${param.name}</h5>
      <p class="card-text">${param.address}</p>
    </div>
  </div>
</body>
</html>
