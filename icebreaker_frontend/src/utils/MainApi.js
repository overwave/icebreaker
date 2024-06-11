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

  // Базовый запрос без тела
  _fetch(way, methodName) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      credentials: 'include'
    }).then(this._checkResponse);
  }

  // Запрос с телом
  _fetchWithBody(way, methodName, bodyContent) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      body: JSON.stringify(bodyContent),
      credentials: 'include'
    }).then(this._checkResponse);
  }

  // Запрос с телом FormData
  _fetchWithBodyFD(way, methodName, bodyContent) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      body: bodyContent,
      credentials: 'include'
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
    formData.append('username', login);
    formData.append('password', password);
    formData.append('remember-me', 'true');

    return this._fetchWithBodyFD("/user/login", "POST", formData);
  }

  // Разлогин
  logout() {
    return this._fetch("/user/logout", "POST");
  }

  // Список кораблей
  getUserInfo() {
    return this._fetch("/user/me", "GET");
  }

  // Список кораблей
  getShips() {
    return this._fetch("/ship/ships", "GET");
  }

  getNavigationPoints() {
    return this._fetch("/navigation/navigation-points", "GET");
  }
}

// Создаем класс апи
const mainApi = new MainApi({
  baseUrl: "https://overwave.dev/icebreaker/api",
  headers: {
    "content-type": "application/json"
  },
});

export default mainApi;
