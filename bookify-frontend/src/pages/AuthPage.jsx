import React, { useState } from "react";
import styled, { keyframes } from "styled-components";
import { FaPlaneDeparture, FaHotel, FaCalendarCheck } from "react-icons/fa";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { attachJwt } from "../api/axios";

/* ─────────────────────────  styling helpers  ───────────────────── */
const pageBlue = "#d8edff",
  cardBlue = "#b4d0ff",
  inputBlue = "#e6f2ff",
  accent1 = "#175dff",
  accent2 = "#1c8bff",
  textDark = "#003b70";

const fadeIn = keyframes`from{opacity:0;transform:translateY(15px)}
                         to{opacity:1;transform:translateY(0)}`;

/* ─────────────────────────  layout elements  ───────────────────── */
const Wrapper = styled.div`display:flex;min-height:100vh;font-family:"Segoe UI",Tahoma,Verdana,sans-serif`;
const ImageSide = styled.div`
  flex:1;background:url("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1352&q=80") center/cover;
  position:relative;&::after{content:"";position:absolute;inset:0;background:rgba(0,0,0,.25)}`;
const FormSide = styled.div`
  flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;
  padding:3rem 2rem;background:${pageBlue}`;

/* brand */
const Brand = styled.h1`
  display:flex;align-items:center;gap:.5rem;font-size:3rem;font-weight:800;letter-spacing:-1px;
  color:${accent1};margin:0 .0 .25rem;animation:${fadeIn}.8s ease-out .1s backwards`;
const Tagline = styled.p`
  font-size:1.1rem;color:${textDark};margin-bottom:2.2rem;
  animation:${fadeIn}.8s ease-out .3s backwards`;

/* card */
const Card = styled.div`
  width:100%;max-width:460px;border-radius:20px;padding:3rem 2.75rem;background:${cardBlue};
  box-shadow:0 12px 28px rgba(0,59,112,.18);animation:${fadeIn}.8s ease-out .55s backwards`;
const Title = styled.h2`margin:0 0 1.6rem;font-size:2rem;text-align:center;color:${textDark}`;

/* inputs */
const Input = styled.input`
  width:100%;margin-bottom:1.15rem;padding:.9rem 1rem;border-radius:8px;
  border:1px solid rgba(0,0,0,.05);background:${inputBlue};color:${textDark};
  &::placeholder{color:#4d6b9a}`;
const Select = styled.select`${Input}`; /* inherit same look */
const Row = styled.div`display:flex;gap:1rem`;

/* buttons / links / msgs */
const Button = styled.button`
  width:100%;padding:1rem;margin-top:.2rem;border:none;border-radius:8px;
  background:linear-gradient(135deg,${accent1},${accent2});color:#fff;font-weight:600;cursor:pointer;
  transition:transform .2s,box-shadow .2s;
  &:hover{transform:translateY(-2px);box-shadow:0 8px 22px rgba(23,93,255,.35)}`;
const LinkTxt = styled.span`color:${accent1};text-decoration:underline;cursor:pointer`;
const Small = styled.small`display:block;margin-top:1.1rem;font-size:.9rem;text-align:center;color:${textDark}`;
const Msg = styled.div`
  margin-bottom:1rem;padding:.85rem 1rem;border-radius:8px;font-size:.95rem;
  background:${p => p.error ? "#ffe6e6" : "#e4ffea"};color:${p => p.error ? "#b00020" : "#0e7a29"};
  white-space:pre-line`;

/* icons row */
const IconsRow = styled.div`
  display:flex;gap:1rem;margin-top:1.6rem;justify-content:center;
  svg{font-size:1.6rem;color:${accent1};opacity:0;animation:${fadeIn}.8s ease-out forwards}
  svg:nth-child(1){animation-delay:.7s}svg:nth-child(2){animation-delay:.85s}svg:nth-child(3){animation-delay:1s}`;

const PASS_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

export default function AuthPage() {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({});
  const [msg, setMsg] = useState({ text: "", error: false });
  const navigate = useNavigate();

  const update = (e) => {
    const newForm = { ...form, [e.target.name]: e.target.value };
    console.log("Form updated:", newForm); // Debug form state
    setForm(newForm);
  };
  const resetMsg = () => setMsg({ text: "", error: false });

  const validateSignup = () => {
    const errs = [];
    if (!form.firstName?.trim()) errs.push("First name is required");
    if (!form.lastName?.trim()) errs.push("Last name is required");
    if (!form.email?.match(/^[^@]+@[^@]+\.[^@]+$/)) errs.push("Invalid email");
    if (!PASS_PATTERN.test(form.password || "")) errs.push("Password ≥8 chars, upper, lower, digit");
    if (form.password !== form.confirmPassword) errs.push("Passwords do not match");
    if (!form.age || +form.age < 18) errs.push("You must be at least 18");
    return errs;
  };

  const submit = async (e) => {
    e.preventDefault();
    resetMsg();
    try {
      if (mode === "login") {
        localStorage.removeItem("jwt");
        console.log("Login payload:", form); // Debug payload
        const { data: jwt } = await axios.post("/api/auth/login", form);
        localStorage.setItem("jwt", jwt);
        attachJwt(jwt);

        // Fetch user details to get the role
        const { data: user } = await axios.get("/api/users/me", {
          headers: { Authorization: `Bearer ${jwt}` },
        });

        // Redirect based on role
        if (user.role === "ROLE_ADMIN") {
          navigate("/admin");
        } else {
          navigate("/home");
        }
      } else if (mode === "signup") {
        const errs = validateSignup();
        if (errs.length) return setMsg({ text: errs.join("\\n"), error: true });

        const dto = {
          username: form.username,
          email: form.email,
          password: form.password,
          firstName: form.firstName,
          lastName: form.lastName,
          age: +form.age,
          role: form.role || "ROLE_USER",
        };
        await axios.post("/api/auth/signup", dto);
        setMsg({ text: "Account created! You can log in now.", error: false });
        setMode("login");
        setForm({}); // Ensure password field is cleared
      } else {
        if (!form.email?.trim()) return setMsg({ text: "Email is required", error: true });
        await axios.post("/api/auth/forgot-password", null, { params: { email: form.email } });
        setMsg({ text: "If that email exists, a reset link is on its way.", error: false });
      }
    } catch (err) {
      const errorMessage = err.response?.data?.message || "Server error – please try again";
      setMsg({ text: errorMessage, error: true });
    }
  };

  const titles = { login: "Welcome back", signup: "Create account", forgot: "Forgot password" };
  const buttons = { login: "Log in", signup: "Sign up", forgot: "Send reset link" };

  const fields = () => {
    if (mode === "signup")
      return (
        <>
          <Input name="firstName" placeholder="First name" required onChange={update} value={form.firstName || ""} />
          <Input name="lastName" placeholder="Last name" required onChange={update} value={form.lastName || ""} />
          <Row>
            <Input name="age" type="number" min={18} placeholder="Age" required onChange={update} value={form.age || ""} />
            <Select name="role" value={form.role || "ROLE_USER"} onChange={update}>
              <option value="ROLE_USER">User</option>
              <option value="ROLE_ADMIN">Admin</option>
            </Select>
          </Row>
          <Input name="email" type="email" placeholder="Email" required onChange={update} value={form.email || ""} />
          <Input name="username" placeholder="Username" required onChange={update} value={form.username || ""} />
          <Input name="password" type="password" placeholder="Password" required onChange={update} value={form.password || ""} />
          <Input name="confirmPassword" type="password" placeholder="Confirm password" required onChange={update} value={form.confirmPassword || ""} />
        </>
      );
    if (mode === "forgot")
      return <Input name="email" type="email" placeholder="Your account email" required onChange={update} value={form.email || ""} />;
    return (
      <>
        <Input name="username" placeholder="Username" required onChange={update} value={form.username || ""} />
        <Input name="password" type="password" placeholder="Password" required onChange={update} value={form.password || ""} />
      </>
    );
  };

  return (
    <Wrapper>
      <ImageSide />
      <FormSide>
        <Brand>
          Bookify <FaPlaneDeparture />
        </Brand>
        <Tagline>Find flights, stays & events in one place</Tagline>

        <Card>
          <Title>{titles[mode]}</Title>
          {msg.text && <Msg error={msg.error.toString()}>{msg.text}</Msg>}

          <form onSubmit={submit}>
            {fields()}
            {mode === "login" && (
              <Small style={{ textAlign: "right", marginBottom: "1rem" }}>
                <LinkTxt
                  onClick={() => {
                    setMode("forgot");
                    setForm({});
                    resetMsg();
                  }}
                >
                  Forgot password?
                </LinkTxt>
              </Small>
            )}
            <Button type="submit">{buttons[mode]}</Button>
          </form>

          {mode !== "signup" && (
            <Small>
              New here?{" "}
              <LinkTxt
                onClick={() => {
                  setMode("signup");
                  setForm({});
                  resetMsg();
                }}
              >
                Create one
              </LinkTxt>
            </Small>
          )}
          {mode !== "login" && (
            <Small>
              Already have an account?{" "}
              <LinkTxt
                onClick={() => {
                  setMode("login");
                  setForm({});
                  resetMsg();
                }}
              >
                Log in
              </LinkTxt>
            </Small>
          )}
        </Card>

        <IconsRow>
          <FaPlaneDeparture />
          <FaHotel />
          <FaCalendarCheck />
        </IconsRow>
      </FormSide>
    </Wrapper>
  );
}