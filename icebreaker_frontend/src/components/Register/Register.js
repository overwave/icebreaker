import AuthForm from "../AuthForm/AuthForm";
import { useEffect } from "react";
import useFormAndValidation from "../../hooks/useFormAndValidation";

function Register({ onSubmit, isError, errorMessage }) {
  const { values, errors, isValid, resetForm, handleChange } =
    useFormAndValidation();

  useEffect(() => {
    resetForm(
      {
        role: "",
        login: "",
        password: "",
      },
      {},
      false
    );
  }, [resetForm]);

  function handleSubmit() {
    onSubmit(values);
  }

  return (
    <AuthForm
      onSubmit={handleSubmit}
      title="Добро пожаловать!"
      buttonText="Зарегистрироваться"
      link="/signin"
      linkText="Войти"
      textWithLink="Уже зарегистрированы?"
      data={values}
      isValid={isValid}
      isError={isError}
      errorMessage={errorMessage}
    >
      <div className="auth__field">
        <span className="auth__input-text">Роль</span>
        <input
          name="role"
          className="auth__input"
          type="text"
          onChange={handleChange}
          value={values.role}
          minLength="2"
          maxLength="30"
          pattern="^[А-ЯЁа-яёA-Za-z\s\-]+$"
          required
        />
        <span className="auth__input-error">{errors.role}</span>
      </div>

      <div className="auth__field">
        <span className="auth__input-text">Логин</span>
        <input
          name="login"
          className="auth__input"
          type="text"
          onChange={handleChange}
          value={values.login}
          pattern="^[А-ЯЁа-яёA-Za-z\s\-]+$"
          required
        />
        <span className="auth__input-error">{errors.login || ""}</span>
      </div>

      <div className="auth__field">
        <span className="auth__input-text">Пароль</span>
        <input
          name="password"
          className="auth__input"
          type="password"
          onChange={handleChange}
          value={values.password}
          required
        />
        <span className="auth__input-error">{errors.password || ""}</span>
      </div>
    </AuthForm>
  );
}

export default Register;
