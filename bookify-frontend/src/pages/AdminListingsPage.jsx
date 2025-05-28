import React, { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import axios from "axios";
import Header from "../components/Header";

const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(10px) }
  to   { opacity: 1; transform: translateY(0) }
`;

const Wrapper = styled.div`
  background: #f0f4f8;
  min-height: 100vh;
`;
const Content = styled.div`
  max-width: 1000px;
  margin: 2rem auto;
  animation: ${fadeIn} .5s ease-out;
`;
const TitleBar = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
`;
const Title = styled.h2`margin: 0;`;
const AddButton = styled(Link)`
  padding: 0.6rem 1rem;
  background: #175dff;
  color: #fff;
  border-radius: 5px;
  text-decoration: none;
  &:hover { background: #004bff; }
`;
const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
`;
const Th = styled.th`
  padding: 0.75rem;
  background: #e1eaff;
  text-align: left;
`;
const Td = styled.td`
  padding: 0.75rem;
  border-top: 1px solid #eee;
`;
const ActionLink = styled.span`
  margin-right: 0.5rem;
  color: #175dff;
  cursor: pointer;
  &:hover { text-decoration: underline; }
`;

export default function AdminListingsPage() {
  const { category } = useParams(); // 'hotels'|'flights'|'events'
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // set auth once
  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) return navigate("/");
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  }, [navigate]);

  // fetch list
  useEffect(() => {
    let url = `/api/listings/${category}`;
    axios.get(url)
      .then(res => setItems(res.data))
      .catch(err => setError(
        err.response?.status === 401
          ? "Unauthorized — please log in as admin"
          : "Failed to load data"
      ));
  }, [category]);

  const singular = category.slice(0, -1).toUpperCase();

  return (
    <Wrapper>
      <Header />
      <Content>
        <TitleBar>
          <Title>Manage {singular}</Title>
          <AddButton to={`/admin/${category}/new`}>+ Add New</AddButton>
        </TitleBar>

        {error && <p style={{ color: "red" }}>{error}</p>}

        <Table>
          <thead>
            <tr>
              {category === "hotels" && <>
                <Th>Title</Th><Th>City</Th><Th>Price</Th><Th>Actions</Th>
              </>}
              {category === "flights" && <>
                <Th>Title</Th><Th>Airline</Th><Th>Price</Th><Th>Actions</Th>
              </>}
              {category === "events" && <>
                <Th>Title</Th><Th>Venue</Th><Th>Date</Th><Th>Actions</Th>
              </>}
            </tr>
          </thead>
          <tbody>
            {items.map(item => (
              <tr key={item.id}>
                {category === "hotels" && <>
                  <Td>{item.title}</Td>
                  <Td>{item.city}</Td>
                  <Td>{item.price}</Td>
                  <Td>
                    <Link to={`/admin/hotels/${item.id}/edit`}>Edit</Link>{" "}
                    <ActionLink onClick={() => {
                      axios.delete(`/api/listings/hotels/${item.id}`)
                        .then(() => setItems(items.filter(h => h.id !== item.id)))
                        .catch(() => alert("Delete failed"));
                    }}>Delete</ActionLink>
                  </Td>
                </>}

                {category === "flights" && <>
                  <Td>{item.title}</Td>
                  <Td>{item.airline}</Td>
                  <Td>{item.price}</Td>
                  <Td>
                    <Link to={`/admin/flights/${item.id}/edit`}>Edit</Link>{" "}
                    <ActionLink onClick={() => {
                      axios.delete(`/api/listings/flights/${item.id}`)
                        .then(() => setItems(items.filter(f => f.id !== item.id)))
                        .catch(() => alert("Delete failed"));
                    }}>Delete</ActionLink>
                  </Td>
                </>}

                {category === "events" && <>
                  <Td>{item.title}</Td>
                  <Td>{item.venue}</Td>
                  <Td>{new Date(item.eventDate).toLocaleString()}</Td>
                  <Td>
                    <Link to={`/admin/events/${item.id}/edit`}>Edit</Link>{" "}
                    <ActionLink onClick={() => {
                      axios.delete(`/api/listings/events/${item.id}`)
                        .then(() => setItems(items.filter(e => e.id !== item.id)))
                        .catch(() => alert("Delete failed"));
                    }}>Delete</ActionLink>
                  </Td>
                </>}
              </tr>
            ))}
          </tbody>
        </Table>
      </Content>
    </Wrapper>
  );
}
