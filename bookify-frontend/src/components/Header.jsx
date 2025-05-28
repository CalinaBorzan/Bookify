// src/components/Header.jsx
import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import styled from "styled-components";
import { FaPlaneDeparture, FaUserCircle } from "react-icons/fa";

const headerFrom = "#0048ff",
      headerTo   = "#177dff";

const Bar = styled.header`
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 0.9rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, ${headerFrom}, ${headerTo});
`;

const Logo = styled.div`
  display: flex;
  align-items: center;
  gap: 0.45rem;
  font-size: 1.9rem;
  font-weight: 700;
  color: #fff;
  cursor: pointer;
`;

const Nav = styled.nav`
  display: flex;
  gap: 1.6rem;
  align-items: center;
`;

const NavLink = styled(Link)`
  color: #fff;
  text-decoration: none;
  font-weight: 500;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    bottom: -4px;
    left: 0;
    width: 0;
    height: 2px;
    background: #fff;
    transition: width 0.3s;
  }
  &:hover::after {
    width: 100%;
  }
`;

const Avatar = styled(Link)`
  color: #fff;
  font-size: 1.8rem;
  display: flex;
  align-items: center;
`;

export default function Header() {
  const location = useLocation();
  const navigate = useNavigate();
  const isAdmin = location.pathname.startsWith("/admin");

  function handleLogoClick() {
    // 1) Remove token → log out
    localStorage.removeItem("jwt");
    // 2) Redirect to the public entrypoint
    navigate("/");
  }

  return (
    <Bar>
      <Logo onClick={handleLogoClick}>
        <FaPlaneDeparture /> Bookify
      </Logo>

      <Nav>
        {isAdmin ? (
          <>
            <NavLink to="/admin/flights">Flights</NavLink>
            <NavLink to="/admin/hotels">Hotels</NavLink>
            <NavLink to="/admin/events">Events</NavLink>
          </>
        ) : (
          <>
            <NavLink to="/home">Packages</NavLink>
            <NavLink to="/hotels">Hotels</NavLink>
            <NavLink to="/flights">Flights</NavLink>
            <NavLink to="/events">Events</NavLink>
          </>
        )}
      </Nav>

      <Avatar to="/account">
        <FaUserCircle />
      </Avatar>
    </Bar>
  );
}
