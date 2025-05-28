// src/pages/CategoryPage.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import { FaCalendarAlt } from "react-icons/fa";
import Header from "../components/Header";

/* ── animation ── */
const fade = keyframes`
  from { opacity:0; transform:translateY(15px); }
  to   { opacity:1; transform:translateY(0);    }
`;

/* ── styled components ── */
const Wrapper   = styled.div`padding:2rem; animation:${fade}.5s ease-out;`;
const FilterBar = styled.div`
  display:flex; align-items:center; gap:1rem; margin-bottom:1.6rem; flex-wrap:wrap;
`;
const Select    = styled.select`padding:.55rem; font-size:1rem; min-width:160px;`;
const DateInput = styled.input`
  padding:.55rem; font-size:1rem; min-width:145px;
`;

const Grid = styled.div`
  display:grid; gap:1.6rem;
  grid-template-columns:repeat(auto-fit,minmax(250px,1fr));
`;
const Card = styled.div`
  border-radius:12px; overflow:hidden; cursor:pointer;
  box-shadow:0 4px 12px rgba(0,0,0,.1);
  transition:transform .25s;
  &:hover{transform:translateY(-4px);}
`;
const Img  = styled.img`
  width:100%; height:160px; object-fit:cover; display:block;
`;
const Info   = styled.div`padding:1rem; background:#fff;`;
const Title  = styled.h3`margin:0 0 .45rem; font-size:1.2rem;`;
const Detail = styled.p`margin:.25rem 0; font-size:.95rem; color:#333;`;

/* ── detail modal ── */
const ModalBG      = styled.div`
  position:fixed; inset:0; background:rgba(0,0,0,.5);
  display:${p => p.open ? "flex" : "none"};
  align-items:center; justify-content:center; z-index:1000;
`;
const ModalContent = styled.div`
  background:#fff; padding:2rem; border-radius:10px;
  max-width:400px; width:90%; position:relative;
`;
const CloseBtn     = styled.button`
  position:absolute; top:8px; right:8px;
  background:none; border:none; font-size:1.2rem; cursor:pointer;
`;
const ConfirmBtn   = styled.button`
  display:block; width:100%; margin:1rem 0; padding:.8rem 1.2rem;
  background:#175dff; color:#fff; border:none; border-radius:6px;
  cursor:pointer; font-weight:600;
  &:hover { background:#004bff; }
`;
const GuestInput   = styled.input`
  width:60px; margin-left:.5rem; padding:.3rem; font-size:1rem;
  text-align:center;
`;
const Reviews      = styled.div`margin-top:1.5rem;`;
const Review       = styled.div`
  padding:.75rem; border-bottom:1px solid #eee; font-size:.95rem;
`;

/* ── helpers ── */
const iso = d => d && new Date(d).toISOString().split("T")[0];

export default function CategoryPage({ category }) {
  const [items,      setItems]      = useState([]);
  const [countries,  setCountries]  = useState([]);
  const [country,    setCountry]    = useState("");
  const [from,       setFromDate]   = useState("");
  const [to,         setToDate]     = useState("");
  const [modalOpen,  setModalOpen]  = useState(false);
  const [detailItem, setDetailItem] = useState(null);
  const [reviews,    setReviews]    = useState([]);
  const [guests,     setGuests]     = useState(1);

  const token = localStorage.getItem("jwt");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) return alert("Please log in first.");
    fetch(`/api/listings/${category}`, {
      headers: { Authorization:`Bearer ${token}` }
    })
    .then(r => {
      if (r.status === 401) throw new Error("Session expired");
      return r.json();
    })
    .then(data => {
      setItems(data);
      setCountries([...new Set(data.map(i=>i.country))]);
    })
    .catch(e => alert(e.message));
  }, [category, token]);

  const inWindow = itm => {
    if (!from || !to) return true;
    const availFrom = itm.availableFrom ?? itm.departureTime ?? itm.eventDate;
    const availTo   = itm.availableTo   ?? itm.arrivalTime   ?? itm.eventDate;
    return new Date(iso(to)) >= new Date(availFrom)
        && new Date(iso(from)) <= new Date(availTo);
  };
  const shown = items.filter(i =>
    (!country || i.country===country) && inWindow(i)
  );

  const openDetail = async itm => {
    setDetailItem(itm);
    try {
      const res = await fetch(`/api/reviews/listing/${itm.id}`, {
        headers: { Authorization:`Bearer ${token}` }
      });
      if (res.status === 401) throw new Error("Session expired");
      const data = await res.json();
      setReviews(data);
      setModalOpen(true);
    } catch(err) {
      alert(err.message);
    }
  };

  const handleBooking = async payNow => {
    if (!detailItem) return;
const checkIn  = from && new Date(from).toISOString().split("T")[0];
const checkOut = to   && new Date(to).toISOString().split("T")[0];
if (!checkIn || !checkOut) {
    alert("Please select both check-in and check-out dates.");
    return;
  }
    const dto = {
      userId:     1,
       listingId:  detailItem.id,
  type:       detailItem.listingType,
  checkIn,
  checkOut,
  numGuests:  guests,
  payNow
    };
    try {
      const r = await fetch("/api/bookings", {
        method:"POST",
        headers:{
          "Content-Type":"application/json",
          Authorization:`Bearer ${token}`
        },
        body: JSON.stringify(dto)
      });
      if (!r.ok) throw new Error(`Error ${r.status}`);
      alert("Booking confirmed! Check your email.");
      setModalOpen(false);
    } catch(e) {
      alert(e.message);
    }
  };

  // helper to get remaining capacity
  const capacityLeft = it => {
    if (category === "flights") return it.seatCapacity;
    if (category === "hotels")  return it.totalRooms;
    if (category === "events")  return it.ticketCapacity;
    return Infinity;
  };

  return (
    <>
      <Header/>
      <Wrapper>
        <FilterBar>
          <Select value={country} onChange={e=>setCountry(e.target.value)}>
            <option value="">All countries</option>
            {countries.map(c=>(
              <option key={c} value={c}>{c}</option>
            ))}
          </Select>
          <DateInput type="date" value={from} onChange={e=>setFromDate(e.target.value)}/>
          <span>→</span>
          <DateInput type="date" value={to} onChange={e=>setToDate(e.target.value)}/>
          <FaCalendarAlt/>
        </FilterBar>

        <Grid>
          {shown.map(it=>(
            <Card key={it.id} onClick={()=>openDetail(it)}>
              {(it.imageUrl||it.photo) && (
                <Img src={it.imageUrl||it.photo} alt={it.title}/>
              )}
              <Info>
                <Title>{it.title}</Title>
                {category==="flights" && <>
                  <Detail>{it.departure} → {it.arrival}</Detail>
                  <Detail>Seats left: {it.seatCapacity}</Detail>
                </>}
                {category==="hotels" && <>
                  <Detail>{it.city}</Detail>
                  <Detail>Rooms available: {it.totalRooms}</Detail>
                </>}
                {category==="events" && <>
                  <Detail>{new Date(it.eventDate).toLocaleDateString()}</Detail>
                  <Detail>Tickets left: {it.ticketCapacity}</Detail>
                </>}
              </Info>
            </Card>
          ))}
        </Grid>
      </Wrapper>

      <ModalBG open={modalOpen}>
        <ModalContent>
          <CloseBtn onClick={()=>setModalOpen(false)}>×</CloseBtn>
          {detailItem && (
            <>
              <h2>{detailItem.title}</h2>
              <p>{detailItem.description}</p>
              {(from && to) && (
                <p><strong>Dates:</strong> {from} – {to}</p>
              )}

              <p>
                Guests:
                <GuestInput
                  type="number"
                  min={1}
                  max={capacityLeft(detailItem)}
                  value={guests}
                  onChange={e=>setGuests(Math.min(capacityLeft(detailItem), Math.max(1, +e.target.value||1)))}
                />
                {" "}
                (max {capacityLeft(detailItem)})
              </p>

              <ConfirmBtn onClick={()=>handleBooking(true )}>
                Pay Now
              </ConfirmBtn>
              <ConfirmBtn onClick={()=>handleBooking(false)}>
                Pay on Arrival
              </ConfirmBtn>

             <Reviews>
  <h4>Reviews</h4>
  {reviews.length
    ? reviews.map(r => (
        <Review key={r.id}>
          <strong>{r.author}</strong>: {r.comment}
        </Review>
      ))
    : <p>No reviews yet.</p>}
</Reviews>
            </>
          )}
        </ModalContent>
      </ModalBG>
    </>
  );
}
