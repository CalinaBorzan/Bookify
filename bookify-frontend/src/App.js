import React, { useEffect, useState } from "react";
import "./App.css";
import {
  BrowserRouter,
  Routes,
  Route,
  useNavigate,
} from "react-router-dom";
import axios from "axios";

import AuthPage from "./pages/AuthPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import HomePage from "./pages/HomePage";
import AccountPage from "./pages/AccountPage";
import CountryPage from "./pages/CountryPage";
import PackagePage from "./pages/PackagePage";
import CategoryPage from "./pages/CategoryPage";
import BookPage from "./pages/BookPage";
import AdminPage from "./pages/AdminPage";
import AdminListingsPage from "./pages/AdminListingsPage";
import AdminEditListingPage from "./pages/AdminEditListingPage";

// ProtectedRoute unchanged
function ProtectedRoute({ children, allowedRole = null }) {
  const [ok, setOk] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) return navigate("/");
    axios
      .get("/api/users/me", { headers: { Authorization: `Bearer ${token}` } })
      .then(({ data }) => {
        if (allowedRole && data.role !== allowedRole) {
          navigate("/home");
        } else {
          setOk(true);
        }
      })
      .catch(() => navigate("/"));
  }, [navigate, allowedRole]);

  return ok ? children : null;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/" element={<AuthPage />} />
        <Route path="/" element={<AuthPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />

        {/* User-only */}
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <HomePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/account"
          element={
            <ProtectedRoute>
              <AccountPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/destinations/:code"
          element={
            <ProtectedRoute>
              <CountryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/packages/:pid"
          element={
            <ProtectedRoute>
              <PackagePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/flights"
          element={
            <ProtectedRoute>
              <CategoryPage category="flights" />
            </ProtectedRoute>
          }
        />
        <Route
          path="/hotels"
          element={
            <ProtectedRoute>
              <CategoryPage category="hotels" />
            </ProtectedRoute>
          }
        />
        <Route
          path="/events"
          element={
            <ProtectedRoute>
              <CategoryPage category="events" />
            </ProtectedRoute>
          }
        />
        <Route
          path="/book/:category/:id"
          element={
            <ProtectedRoute>
              <BookPage />
            </ProtectedRoute>
          }
        />

        {/* Admin console */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRole="ROLE_ADMIN">
              <AdminPage />
            </ProtectedRoute>
          }
        />

        {/* Listings management */}
        <Route
          path="/admin/:category"
          element={
            <ProtectedRoute allowedRole="ROLE_ADMIN">
              <AdminListingsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/:category/new"
          element={
            <ProtectedRoute allowedRole="ROLE_ADMIN">
              <AdminEditListingPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/:category/:id/edit"
          element={
            <ProtectedRoute allowedRole="ROLE_ADMIN">
              <AdminEditListingPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
