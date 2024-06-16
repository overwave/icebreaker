import "./Header.css";
import logo from "../../images/logo.svg";
import { useContext, useState, useEffect } from "react";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";
import avatar from "../../images/avatar.svg";
import logout from "../../images/logout.svg";

export default function Header({ loggedIn, handleLogout }) {
  const currentUser = useContext(CurrentUserContext);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if (isOpen) {
      window.addEventListener("click", closeMenu);
      return () => window.removeEventListener("click", closeMenu);
    }
  }, [isOpen]);

  function toggleMenu() {
    setIsOpen(!isOpen);
  }

  function closeMenu(e) {
    const el = e.target;
    if (
      el.className !== "header__menu" &&
      el.className !== "header__img" &&
      el.className !== "header__user" &&
      el.className !== "header__avatar"
    ) {
      setIsOpen(false);
    }
  }

  if (loggedIn) {
    return (
      <header className="header">
        <img className="header__logo" src={logo} alt="Логотип" />
        <div className="header__user">
          <span className="header__name">{currentUser.currentUser.login}</span>
          <img
            className="header__avatar"
            src={avatar}
            alt="Аватар"
            onClick={toggleMenu}
          />
          {isOpen && (
            <div className="header__menu" onClick={handleLogout}>
              <img className="header__img" src={logout} alt="" />
              Выйти
            </div>
          )}
        </div>
      </header>
    );
  } else {
    return <></>;
  }
}
