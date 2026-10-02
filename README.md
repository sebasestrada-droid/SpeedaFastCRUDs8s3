# 🚚 SpeedFast – Gestión de Pedidos

Aplicación de escritorio desarrollada en **Java** para la gestión de clientes, repartidores, pedidos y entregas de la empresa ficticia **SpeedFast**.

El proyecto utiliza **MySQL** como sistema de gestión de base de datos y una interfaz gráfica desarrollada con **Java Swing**, permitiendo realizar operaciones de registro, consulta, actualización y eliminación de información.

---

## 📌 Descripción

El sistema permite administrar de manera sencilla la información relacionada con el proceso de entrega de pedidos.

La aplicación cuenta con diferentes módulos que permiten gestionar:

- 👤 Clientes
- 🚴 Repartidores
- 📦 Pedidos
- 🚚 Entregas

Los datos son almacenados en una base de datos **MySQL**, permitiendo que la información registrada desde la aplicación quede persistida.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|------------|-----|
| ☕ Java | Lenguaje principal |
| 🖥️ Java Swing | Interfaz gráfica |
| 🗄️ MySQL | Base de datos |
| 📦 Maven | Gestión del proyecto y dependencias |
| 💻 IntelliJ IDEA | Entorno de desarrollo |

---

## 🏗️ Estructura del proyecto

```text
src/
└── main/
    ├── java/
    │   ├── app/
    │   ├── dao/
    │   ├── model/
    │   ├── util/
    │   └── view/
    │
    └── resources/

test/
