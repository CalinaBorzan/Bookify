import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import axios from "axios";
import Header from "../components/Header";

const bg = "#f0f4f8";
const card = "#fff";
const accent = "#175dff";
const fade = keyframes`
  from { opacity:0; transform:translateY(10px) }
  to   { opacity:1; transform:translateY(0) }
`;

const Wrapper = styled.div`
  background: ${bg};
  min-height: 100vh;
`;
const Box = styled.div`
  max-width: 500px;
  margin: 2rem auto;
  background: ${card};
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  animation: ${fade} .5s ease-out;
`;
const Title = styled.h2`
  color: ${accent};
  text-align: center;
`;
const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;
const Field = styled.div`
  display: flex;
  flex-direction: column;
`;
const Label = styled.label`
  font-weight: 600;
`;
const Input = styled.input`
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
`;
const Select = styled.select`
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
`;
const BtnRow = styled.div`
  display: flex;
  gap: 0.5rem;
  margin-top: 1.5rem;
`;
const Button = styled.button`
  flex: 1;
  padding: 0.8rem;
  background: ${p => p.$secondary ? "#aaa" : accent};
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  &:hover {
    background: ${p => p.$secondary ? "#888" : "#004bff"};
  }
`;
const Error = styled.p`
  color: #e92365;
  text-align: center;
`;

export default function AdminEditUserPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    firstName: "",
    lastName: "",
    age: "",
    role: "USER",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) return navigate("/login");
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

    axios
      .get(`/api/users/${id}`)
      .then(({ data }) => {
        setForm({
          username: data.username,
          email: data.email,
          firstName: data.firstName || "",
          lastName: data.lastName || "",
          age: data.age || "",
          role: data.role,
        });
      })
      .catch(() => setError("Could not load user."))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const onChange = e =>
    setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const onSubmit = e => {
    e.preventDefault();
    const dto = {
      email: form.email,
      firstName: form.firstName,
      lastName: form.lastName,
      age: parseInt(form.age, 10) || null,
      role: form.role,
    };
    axios
      .put(`/api/users/${id}`, dto)
      .then(() => navigate("/admin/users"))
      .catch(() => setError("Save failed."));
  };

  if (loading) {
    return (
      <Wrapper>
        <Header/>
        <Box><p>Loading…</p></Box>
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <Header/>
      <Box>
        <Title>Edit User</Title>
        {error && <Error>{error}</Error>}
        <Form onSubmit={onSubmit}>
          <Field>
            <Label>Username</Label>
            <Input name="username" value={form.username} disabled />
          </Field>
          <Field>
            <Label>Email</Label>
            <Input
              type="email"
              name="email"
              value={form.email}
              onChange={onChange}
              required
            />
          </Field>
          <Field>
            <Label>First Name</Label>
            <Input
              name="firstName"
              value={form.firstName}
              onChange={onChange}
            />
          </Field>
          <Field>
            <Label>Last Name</Label>
            <Input
              name="lastName"
              value={form.lastName}
              onChange={onChange}
            />
          </Field>
          <Field>
            <Label>Age</Label>
            <Input
              type="number"
              name="age"
              value={form.age}
              onChange={onChange}
            />
          </Field>
          <Field>
            <Label>Role</Label>
            <Select name="role" value={form.role} onChange={onChange}>
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </Select>
          </Field>
          <BtnRow>
            <Button $secondary onClick={()=>navigate("/admin/users")}>
              Cancel
            </Button>
            <Button type="submit">Save</Button>
          </BtnRow>
        </Form>
      </Box>
    </Wrapper>
  );
}
