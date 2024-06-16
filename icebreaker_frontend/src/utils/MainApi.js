class MainApi {
  constructor({ baseUrl, headers }) {
    this._url = baseUrl;
    this._headers = headers;
  }

  _checkResponse(res) {
    if (res.ok) {
      return res.json();
    }

    return Promise.reject(res);
  }

  _checkResponseFILE(res) {
    if (res.ok) {
      return res.blob();
    }

    return Promise.reject(res);
  }

  // Базовый запрос без тела
  _fetch(way, methodName) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      credentials: "include",
    }).then(this._checkResponse);
  }

  // Базовый запрос без тела FILE
  _fetchFile(way, methodName) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      credentials: "include",
    }).then(this._checkResponseFILE);
  }

  // Запрос с телом
  _fetchWithBody(way, methodName, bodyContent) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      body: JSON.stringify(bodyContent),
      credentials: "include",
    }).then(this._checkResponse);
  }

  // Запрос с телом FormData
  _fetchWithBodyFD(way, methodName, bodyContent) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      body: bodyContent,
      credentials: "include",
    }).then(this._checkResponse);
  }

  // Регистрация
  register({ role, login, password }) {
    return this._fetchWithBody("/user/register", "POST", {
      role: role,
      login: login,
      password: password,
    });
  }

  // Авторизация
  authorize({ login, password }) {
    const formData = new FormData();
    formData.append("username", login);
    formData.append("password", password);
    formData.append("remember-me", "true");

    return this._fetchWithBodyFD("/user/login", "POST", formData);
  }

  // Разлогин
  logout() {
    return this._fetch("/user/logout", "POST");
  }

  // Информация о юзере
  getUserInfo() {
    return this._fetch("/user/me", "GET");
  }

  // Список кораблей
  getShips() {
    return this._fetch("/ship/ships", "GET");
  }

  // Список опорных точек
  getNavigationPoints() {
    return this._fetch("/navigation/navigation-points", "GET");
  }

  // Заявки
  getAllApplications() {
    return this._fetch("/navigation/route-requests", "GET");
  }

  // Подача новой заявки
  setNewApplication({ shipId, startPointId, finishPointId, startDate }) {
    return this._fetchWithBody("/navigation/route-requests", "PUT", {
      shipId: shipId,
      startPointId: startPointId,
      finishPointId: finishPointId,
      startDate: startDate,
    });
  }

  // Ледовые классы
  getIceClasses() {
    return this._fetch("/ship/ice-classes", "GET");
  }

  // Новое судно
  setNewShip({ name, speed, iceClass }) {
    return this._fetchWithBody("/ship/ships", "PUT", {
      name: name,
      speed: speed,
      iceClass: iceClass,
    });
  }

  // Получить выбранный маршрут корабля
  getShipRoute({ id }) {
    return this._fetch(
      `/navigation/ship-route?navigationRequestId=${id}`,
      "GET"
    );
  }

  // Получить выбранный маршрут ледокола
  getIceRoute(id) {
    return this._fetch(`/icebreaker/route?icebreakerId=${id}`, "GET");
  }

  // Информация о ледоколах
  getAllIcebreaker() {
    return this._fetch("/icebreaker/all", "GET");
  }

  // Формирование маршрута
  setNewRouteList() {
    return this._fetch("/schedule/schedules", "PUT");
  }

  // Скачать согласованные маршруты
  getGantt() {
    return this._fetchFile("/schedule/gantt", "GET");
  }

  // Скачать маршрут ледокола
  getIceGantt(id) {
    return this._fetchFile(
      `/schedule/gantt/icebreaker?icebreakerId=${id}`,
      "GET"
    );
  }
}

// Создаем класс апи
const mainApi = new MainApi({
  baseUrl: "https://overwave.dev/icebreaker/api",
  headers: {
    "content-type": "application/json",
  },
});

export default mainApi;
