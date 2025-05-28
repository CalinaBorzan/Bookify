// src/api/axiosConfig.js
import axios from "axios";

// ① pick the token from localStorage (or however you store it)
const token = localStorage.getItem("jwt");
if (token) {
  axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

// ② if the user logs-in later in the session, update automatically
export const attachJwt = (jwt) => {
  axios.defaults.headers.common["Authorization"] = `Bearer ${jwt}`;
};
