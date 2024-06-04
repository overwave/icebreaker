import React from "react";
import "./PageNotFound.css";

function PageNotFound({ history }) {
  return (
    <section className="error">
      <div className="error__cap"></div>
      <div className="error__info">
        <h1 className="error__title">404</h1>
        <p className="error__subtitle">Страница не найдена</p>
      </div>
      <nav className="error__nav">
        <button
          type="button"
          onClick={() => history.push("/")}
          className="error__link"
        >
          Назад
        </button>
      </nav>
    </section>
  );
}

export default PageNotFound;
