import "./Header.css";
import logo from "../../images/logo.svg";
import { useContext } from "react";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";

export default function Header({ loggedIn, handleLogout }) {
    const currentUser = useContext(CurrentUserContext);

    if (loggedIn) {
        return (
            <header className="header">
                <img className="header__logo" src={logo} alt="Логотип" />
                <div className="header__user">
                    <span className="header__name">{currentUser.currentUser.login}</span>
                    <span className="header__circle" onClick={handleLogout}>Выйти</span>
                </div>
            </header>
        );
    } else {
        return <></>;
    }
}