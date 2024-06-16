import AuthForm from "../AuthForm/AuthForm";
import { useEffect, useState } from "react";
import useFormAndValidation from "../../hooks/useFormAndValidation";
import eye from "../../images/eye.svg";
import eye2 from "../../images/eye2.svg";
import CustomSelect from "../CustomSelect/CustomSelect";
import { roles } from "../../configs/constants";

function Register({ onSubmit, isError, errorMessage, changeOption }) {
  const { values, errors, resetForm, handleChange } =
    useFormAndValidation();
  const [isEye, setIsEye] = useState(false);
  const [selectedRole, setSelectedRole] = useState(undefined);
  const [isValidForm, setIsValidForm] = useState(false);

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

  useEffect(() => {
    if (selectedRole !== undefined) {
      setIsValidForm(true);
    }
  }, [selectedRole]);

  function handleSubmit() {
    values.role = roles[selectedRole].role;
    onSubmit(values);
  }

  function handleEye() {
    setIsEye(!isEye);
  }

  return (
    <AuthForm
      onSubmit={handleSubmit}
      title="Регистрация"
      buttonText="Зарегистрироваться"
      link="/icebreaker/signin"
      linkText="Войти"
      textWithLink="Уже есть аккаунт? "
      data={values}
      isValid={isValidForm}
      isError={isError}
      errorMessage={errorMessage}
    >
      <div className="auth__field">
        <CustomSelect
          myClass="auth__select"
          options={roles}
          selectedOption={selectedRole}
          changeOption={changeOption}
          name="selectedRole"
          setSelected={setSelectedRole}
          clue="Роль"
        />
        <span className="auth__input-error">{errors.role}</span>
      </div>

      <div className="auth__field">
        <label
          className={`auth__label ${
            values.login === "" ? "" : "auth__label_active"
          }`}
        >
          Логин
        </label>
        <input
          name="login"
          className={`auth__input ${
            values.login === "" ? "" : "auth__input_active"
          } ${errors.login ? "auth__input_error" : ""}`}
          type="text"
          onChange={handleChange}
          value={values.login}
          pattern="^[А-ЯЁа-яёA-Za-z\s\-]+$"
          required
        />
        <span className="auth__input-error">{errors.login || ""}</span>
      </div>

      <div className="auth__field">
        <label
          className={`auth__label ${
            values.password === "" ? "" : "auth__label_active"
          }`}
        >
          Пароль
        </label>
        <input
          name="password"
          className={`auth__input ${
            values.password === "" ? "" : "auth__input_active"
          } ${errors.password ? "auth__input_error" : ""}`}
          type={`${isEye ? "text" : "password"}`}
          onChange={handleChange}
          value={values.password}
          required
        />
        <span className="auth__input-error">{errors.password || ""}</span>
        <button type="button" className="auth__eye" onClick={handleEye}>
          <img className="auth__eye-icon" src={isEye ? eye2 : eye} alt="" />
        </button>
      </div>
    </AuthForm>
  );
}

export default Register;
