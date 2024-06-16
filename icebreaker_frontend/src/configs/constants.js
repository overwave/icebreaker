const roles = [
    {
      id: 1,
      name: "Пользователь",
      role: "CAPTAIN"
    },
    {
      id: 2,
      name: "Администратор",
      role: "ADMIN"
    }
  ];

  const applications = [
    {
      name: "Согласованные",
      type: "agreed"
    },
    {
      name: "В обработке",
      type: "pending"
    },
    {
      name: "Архив",
      type: "archive"
    }
  ];
  const adminApplications = [
    {
      name: "Новые",
      type: "pending"
    },
    {
      name: "Согласованные",
      type: "agreed"
    },
    {
      name: "Архив",
      type: "archive"
    }
  ];
  const months = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
  const nodeCases = ["узел", "узла", "узлов"];

  module.exports = { roles, applications, adminApplications, months, nodeCases };