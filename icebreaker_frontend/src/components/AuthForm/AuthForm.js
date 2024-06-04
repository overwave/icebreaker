import "./AuthForm.css";
import { NavLink } from "react-router-dom";

function AuthForm({
  onSubmit,
  title,
  buttonText,
  buttonClass = "",
  link,
  linkText,
  textWithLink,
  data,
  isValid,
  isError,
  errorMessage,
  children,
}) {
  function handleSubmit(e) {
    e.preventDefault();
    onSubmit(data);
  }

  return (
    <section className="auth">
      <form className="auth__form" onSubmit={handleSubmit}>
        <div className="auth__block">
          <h1 className="auth__title">{title}</h1>
          {children}
        </div>
        <div className="auth__block">
          {isError && <span className="auth__error">{errorMessage}</span>}
          <button
            type="submit"
            className={`auth__btn ${
              !isValid ? "auth__btn_disabled" : ""
            } ${buttonClass}`}
            aria-label={title}
            disabled={!isValid}
          >
            {buttonText}
          </button>
          <p className="auth__text">
            {textWithLink}
            <NavLink className="auth__link" to={link}>
              {linkText}
            </NavLink>
          </p>
        </div>
      </form>
    </section>
  );
}

export default AuthForm;
