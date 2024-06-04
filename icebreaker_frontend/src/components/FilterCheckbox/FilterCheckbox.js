import "./FilterCheckbox.css";

function FilterCheckbox({ handleShortFilms, isShortFilm }) {
  return (
    <div action="search-filter" className="search__filter">
      <input
        type="checkbox"
        id="checkbox"
        className="search__checkbox"
        checked={isShortFilm}
        onChange={handleShortFilms}
      />
      <label htmlFor="checkbox" className="search__label">
        Короткометражки
      </label>
    </div>
  );
}

export default FilterCheckbox;
