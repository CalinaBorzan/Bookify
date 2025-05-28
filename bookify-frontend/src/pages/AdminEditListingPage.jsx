// src/pages/AdminEditListingPage.jsx
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import axios from "axios";
import Header from "../components/Header";

const pageBlue = "#f0f4f8";
const fadeIn = keyframes`
  from { opacity:0; transform:translateY(10px) }
  to   { opacity:1; transform:translateY(0) }
`;
const accent = "#175dff";
const cardBg = "#ffffff";
const textDark = "#333";

const Wrapper = styled.div`
  background: ${pageBlue};
  min-height: 100vh;
`;
const ContentWrapper = styled.div`
  background: ${cardBg};
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  animation: ${fadeIn} .5s ease-out;
`;
const Title = styled.h2`
  margin-top: 0;
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
  margin-bottom: 0.25rem;
  color: ${textDark};
`;
const Input = styled.input`
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
`;
const Textarea = styled.textarea`
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  resize: vertical;
`;
const ButtonRow = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
`;
const Button = styled.button`
  flex: 1;
  padding: 0.8rem 1rem;
  margin: 0 0.25rem;
  background: ${p => p.$secondary ? "#aaa" : accent};
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  &:hover { background: ${p => p.$secondary ? "#888" : "#004bff"}; }
`;
const Error = styled.p`
  color: #e92365;
  text-align: center;
`;

export default function AdminEditListingPage() {
  const { category, id } = useParams();
  const isNew = !id;
  const navigate = useNavigate();

  const [form, setForm] = useState({
    title: "", country: "", description: "", price: "",
    address: "", city: "", starRating: "", totalRooms: "", availableFrom: "", availableTo: "",
    airline: "", departure: "", arrival: "", departureTime: "", arrivalTime: "", seatCapacity: "",
    venue: "", eventDate: "", ticketCapacity: ""
  });
  const [loading, setLoading] = useState(!isNew);
  const [error, setError] = useState(null);

  // set auth header
  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) return navigate("/login");
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  }, [navigate]);

  // fetch existing
  useEffect(() => {
    if (!isNew) {
      axios.get(`/api/listings/${category}/${id}`)
        .then(({ data: d }) => {
          setForm({
            title: d.title || "",
            country: d.country || "",
            description: d.description || "",
            price: d.price ?? "",
            address: d.address || "",
            city: d.city || "",
            starRating: d.starRating ?? "",
            totalRooms: d.totalRooms ?? "",
            availableFrom: d.availableFrom?.slice(0,10) || "",
            availableTo: d.availableTo?.slice(0,10) || "",
            airline: d.airline || "",
            departure: d.departure || "",
            arrival: d.arrival || "",
            departureTime: d.departureTime?.slice(0,16) || "",
            arrivalTime: d.arrivalTime?.slice(0,16) || "",
            seatCapacity: d.seatCapacity ?? "",
            venue: d.venue || "",
            eventDate: d.eventDate?.slice(0,16) || "",
            ticketCapacity: d.ticketCapacity ?? ""
          });
          setLoading(false);
        })
        .catch(() => {
          setError("Failed to load listing. Please try again.");
          setLoading(false);
        });
    }
  }, [category, id, isNew]);

const handleChange = e => {
  const { name, value } = e.target;
  setForm(f => ({ ...f, [name]: value }));
};

  const handleSubmit = e => {
  e.preventDefault();

  // ensure "YYYY-MM-DDTHH:mm" becomes "YYYY-MM-DDTHH:mm:00"
  const fixDT = dt =>
    typeof dt === "string" && dt.length === 16 ? dt + ":00" : dt;

  const payload = {
    ...form,
    price:          parseFloat(form.price),
    starRating:     parseInt(form.starRating, 10) || null,
    totalRooms:     parseInt(form.totalRooms, 10) || null,
    seatCapacity:   parseInt(form.seatCapacity, 10) || null,
    ticketCapacity: parseInt(form.ticketCapacity, 10) || null,
    departureTime:  fixDT(form.departureTime),
    arrivalTime:    fixDT(form.arrivalTime),
    eventDate:      fixDT(form.eventDate),
  };

    const method = isNew ? "post" : "put";
    const url = isNew
      ? `/api/listings/${category}`
      : `/api/listings/${category}/${id}`;

    axios({ method, url, data: payload })
      .then(() => navigate(`/admin/${category}`))
      .catch(() => setError("Save failed. Check your inputs and try again."));
  };

  if (loading) {
    return (
      <Wrapper>
        <Header />
        <ContentWrapper><p>Loading…</p></ContentWrapper>
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <Header />
      <ContentWrapper>
        <Title>{isNew ? "Add New" : "Edit"} {category.slice(0,-1).toUpperCase()}</Title>
        {error && <Error>{error}</Error>}
        <Form onSubmit={handleSubmit}>
          {/* common */}
          <Field>
            <Label>Title</Label>
            <Input name="title" value={form.title} onChange={handleChange} required/>
          </Field>
          <Field>
            <Label>Country</Label>
            <Input name="country" value={form.country} onChange={handleChange} required/>
          </Field>
          <Field>
            <Label>Description</Label>
            <Textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              rows={3}
              required
            />
          </Field>
          <Field>
            <Label>Price</Label>
            <Input
              type="number"
              step="0.01"
              name="price"
              value={form.price}
              onChange={handleChange}
              required
            />
          </Field>

          {/* hotels */}
          {category === "hotels" && <>
            <Field><Label>Address</Label>
              <Input name="address" value={form.address} onChange={handleChange} required/>
            </Field>
            <Field><Label>City</Label>
              <Input name="city" value={form.city} onChange={handleChange} required/>
            </Field>
            <Field><Label>Star Rating</Label>
              <Input
                type="number"
                min="1" max="5"
                name="starRating"
                value={form.starRating}
                onChange={handleChange}
              />
            </Field>
            <Field><Label>Total Rooms</Label>
              <Input
                type="number"
                name="totalRooms"
                value={form.totalRooms}
                onChange={handleChange}
                required
              />
            </Field>
            <Field><Label>Available From</Label>
              <Input
                type="date"
                name="availableFrom"
                value={form.availableFrom}
                onChange={handleChange}
                required
              />
            </Field>
            <Field><Label>Available To</Label>
              <Input
                type="date"
                name="availableTo"
                value={form.availableTo}
                onChange={handleChange}
                required
              />
            </Field>
          </>}

          {/* flights */}
          {category === "flights" && <>
            <Field><Label>Airline</Label>
              <Input name="airline" value={form.airline} onChange={handleChange} required/>
            </Field>
            <Field><Label>Departure</Label>
              <Input name="departure" value={form.departure} onChange={handleChange} required/>
            </Field>
            <Field><Label>Arrival</Label>
              <Input name="arrival" value={form.arrival} onChange={handleChange} required/>
            </Field>
            <Field><Label>Departure Time</Label>
              <Input
                type="datetime-local"
                step="1"
                name="departureTime"
                value={form.departureTime}
                onChange={handleChange}
                required
              />
            </Field>
            <Field><Label>Arrival Time</Label>
              <Input
                type="datetime-local"
                step="1"
                name="arrivalTime"
                value={form.arrivalTime}
                onChange={handleChange}
                required
              />
            </Field>
            <Field><Label>Seat Capacity</Label>
              <Input
                type="number"
                name="seatCapacity"
                value={form.seatCapacity}
                onChange={handleChange}
                required
              />
            </Field>
          </>}

          {/* events */}
          {category === "events" && <>
            <Field><Label>Venue</Label>
              <Input name="venue" value={form.venue} onChange={handleChange} required/>
            </Field>
            <Field><Label>Event Date & Time</Label>
              <Input
                type="datetime-local"
                step="1"
                name="eventDate"
                value={form.eventDate}
                onChange={handleChange}
                required
              />
            </Field>
            <Field><Label>Ticket Capacity</Label>
              <Input
                type="number"
                name="ticketCapacity"
                value={form.ticketCapacity}
                onChange={handleChange}
                required
              />
            </Field>
          </>}

          <ButtonRow>
            <Button $secondary type="button" onClick={() => navigate(`/admin/${category}`)}>
              Cancel
            </Button>
            <Button type="submit">Save</Button>
          </ButtonRow>
        </Form>
      </ContentWrapper>
    </Wrapper>
  );
}
