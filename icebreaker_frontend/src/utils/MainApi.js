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
    }).then(this._checkResponse);
  }

  // Запрос с телом
  _fetchWithBody(way, methodName, bodyContent) {
    return fetch(`${this._url}${way}`, {
      method: methodName,
      headers: this._headers,
      body: JSON.stringify(bodyContent),
    }).then(this._checkResponse);
  }

  // Получаем массив всех сохраненных фильмов
  getAllFilms() {
    this._headers = {
      ...this._headers,
      authorization: `Bearer ${localStorage.getItem("jwt")}`,
    };
    return this._fetch("/movies", "GET");
  }

  // Создаем фильм
  addNewFilm(newFilm) {
    return this._fetchWithBody("/movies", "POST", newFilm);
  }

  // Удаляем фильм из сохраненных
  deleteMovie(movieId) {
    return this._fetch(`/movies/${movieId}`, "DELETE");
  }

  // Получаем всю информацию о пользователе
  getUserInfo() {
    this._headers = {
      ...this._headers,
      authorization: `Bearer ${localStorage.getItem("jwt")}`,
    };
    return this._fetch("/users/me", "GET");
  }

  // Обновляем информацию пользователя
  setUserInfo(newUserInfo) {
    return this._fetchWithBody("/users/me", "PATCH", newUserInfo);
  }

  // Регистрация
  register({ name, email, password }) {
    return this._fetchWithBody("/signup", "POST", {
      name: name,
      email: email,
      password: password,
    });
  }

  // Авторизация
  authorize({ email, password }) {
    return this._fetchWithBody("/signin", "POST", {
      email: email,
      password: password,
    });
  }

  getContent = (jwt) => {
    return fetch(`${this._url}/user/me`, {
      method: "GET",
      headers: {
        Accept: "applications/json",
        "Content-type": "applications/json",
        Authorization: `Bearer ${jwt}`,
      },
    }).then(this._checkResponse);
  };
}

// Создаем класс апи
const mainApi = new MainApi({
  baseUrl: "https://overwave.dev/icebreaker/api",
  headers: {
    "content-type": "application/json",
    authorization: `Bearer ${localStorage.getItem("jwt")}`,
  },
});

export default mainApi;
