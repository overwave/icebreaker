export const errors = {
    404: "404 Страница по указанному маршруту не найдена.",
    500: "500 На сервере произошла ошибка",
}

export const errorsRegister = {
    400: "При регистрации пользователя произошла ошибка.",
    409: "Пользователь с таким логином уже существует.",
}

export const errorsLogin = {
    400: "При авторизации произошла ошибка.",
    401: "Вы ввели неправильный логин или пароль.",
    403: "При авторизации произошла ошибка. Вы ввели неправильный логин или пароль.",
}

export const errorsUpdate = {
    400: "При обновлении профиля произошла ошибка.",
    409: "Пользователь с таким логином уже существует.",
}

export const errorsApplication = {
    points: "Пункт отплытия и пункт прибытия не могут быть одинаковыми."
};

export const errorsShips = {
    name: "Название судна должно быть не менее 2 символов",
    speed: "Введите значение от 5 до 25"
};
