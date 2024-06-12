import AuthForm from "../AuthForm/AuthForm";
import { useEffect, useState } from "react";
import useFormAndValidation from "../../hooks/useFormAndValidation";
import eye from "../../images/eye.svg";
import eye2 from "../../images/eye2.svg";

function Login({ onSubmit, isError, errorMessage }) {
  const { values, errors, isValid, resetForm, handleChange } =
    useFormAndValidation();
  const [isEye, setIsEye] = useState(false);

  useEffect(() => {
    resetForm(
      {
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

  function handleEye() {
    setIsEye(!isEye);
  }

  return (
    <AuthForm
      onSubmit={handleSubmit}
      title="Вход"
      buttonText="Войти"
      buttonClass="auth__btn_name_login"
      link="/icebreaker/signup"
      linkText="Зарегистрироваться"
      textWithLink="Нет аккаунта? "
      data={values}
      isValid={isValid}
      isError={isError}
      errorMessage={errorMessage}
    >
      <div className="auth__field">
        <label className={`auth__label ${values.login === "" ? "":"auth__label_active"}`} htmlFor="login">Логин</label>
        <input
          name="login"
          className={`auth__input ${values.login === "" ? "":"auth__input_active"} ${errors.login ? "auth__input_error":""}`}
          type="text"
          onChange={handleChange}
          value={values.login}
          pattern="^[А-ЯЁа-яёA-Za-z\s\-]+$"
          required
        />
        <span className="auth__input-error">{errors.login || ""}</span>
      </div>

      <div className="auth__field">
        <label className={`auth__label ${values.password === "" ? "":"auth__label_active"}`} htmlFor="password">Пароль</label>
        <input
          name="password"
          className={`auth__input ${values.password === "" ? "":"auth__input_active"} ${errors.password ? "auth__input_error":""}`}
          type={`${isEye ? "text":"password"}`}
          onChange={handleChange}
          value={values.password}
          required
        />
        <span className="auth__input-error">{errors.password || ""}</span>
        <button type="button" className="auth__eye" onClick={handleEye}>
          <img className="auth__eye-icon" src={isEye ? eye2:eye} alt="" />
        </button>
      </div>
    </AuthForm>
  );
}

export default Login;
