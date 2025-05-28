import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import styled, { keyframes } from "styled-components";

const fadeIn = keyframes`
  from { opacity:0; transform:translateY(10px); }
  to   { opacity:1; transform:translateY(0); }
`;

const Wrapper = styled.div`
  max-width:1000px; margin:2rem auto; padding:0 1.5rem;
  font-family:"Segoe UI",sans-serif;
  animation:${fadeIn} .5s ease-out;
`;

const Banner = styled.div`
  height:200px; background:${p=>p.$gradient};
  border-radius:16px; position:relative;
  display:flex; align-items:center; justify-content:center;
  padding:1rem; box-shadow:0 4px 12px rgba(0,0,0,.1);
  @media(max-width:768px){height:150px;}
`;
const BannerIcon  = styled.span`
  position:absolute; top:1rem; left:1rem; font-size:2rem;
`;
const BannerTitle = styled.h2`
  margin:0; font-size:1.8rem; font-weight:700;
  color:#fff; text-shadow:0 2px 4px rgba(0,0,0,.3);
`;

const Box   = styled.div`
  background:#fff; border-radius:14px; padding:1.5rem;
  box-shadow:0 6px 18px rgba(0,0,0,.1); margin-bottom:1.5rem;
  font-size:.95rem; color:#42526e; line-height:1.5;
`;
const Label = styled.span`
  font-weight:700; color:#175dff; margin-right:.6rem;
`;
const H2    = styled.h2`
  margin:1.5rem 0 .8rem; font-size:1.8rem; color:#003b70; font-weight:700;
`;
const NotIncluded = styled.p`
  color:#666; font-style:italic; margin:.5rem 0;
`;

const GuestInput   = styled.input`
  width:60px; margin:1rem 0; padding:.3rem; font-size:1rem;
  text-align:center;
`;
const ConfirmBtn   = styled.button`
  flex:1; padding:.8rem 1rem; border:none; border-radius:6px;
  font-weight:600; cursor:pointer;
`;
const PayNowBtn       = styled(ConfirmBtn)`background:#175dff; color:#fff;`;
const PayOnArrivalBtn = styled(ConfirmBtn)`background:#e92365; color:#fff;`;

const Modal     = styled.div`
  position:fixed; inset:0; background:rgba(0,0,0,.5);
  display:${p=>p.$isOpen?"flex":"none"};
  align-items:center; justify-content:center; z-index:1000;
`;
const ModalContent = styled.div`
  background:#fff; padding:2rem; border-radius:10px;
  width:90%; max-width:400px; text-align:center;
  box-shadow:0 4px 12px rgba(0,0,0,.2);
`;
const ErrorMessage = styled.p`color:#e92365; margin-top:1rem;`;

export default function PackagePage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [error,       setError]       = useState(null);
  const [guests,      setGuests]      = useState(2);
  const { state:data } = useLocation();

  if (!data) {
    return <p style={{ textAlign:"center", color:"#e92365" }}>
      No package data.
    </p>;
  }

  const { hotel, flight, event, total, nights, window } = data;
  const hasFlight = !!flight, hasEvent = !!event;
  const gradient  = hasFlight
    ? hasEvent ? "linear-gradient(135deg,#175dff,#e92365)"
               : "linear-gradient(135deg,#175dff,#4b9bff)"
    : hasEvent ? "linear-gradient(135deg,#e92365,#ff6b9b)"
               : "linear-gradient(135deg,#003b70,#4b9bff)";
  const icon = hasFlight ? "✈" : hasEvent ? "🎫" : "🏨";

  const postOne = async (listing, type, checkIn, checkOut) => {
    const dto = {
      userId    : 1,  // replace with real user
      listingId : listing.id,
      type,
      checkIn,
      checkOut,
      numGuests : guests,
      payNow    : true
    };
    return fetch("/api/bookings", {
      method:"POST",
      headers:{
        "Content-Type":"application/json",
        "Authorization":`Bearer ${localStorage.getItem("jwt")||""}`
      },
      body:JSON.stringify(dto)
    });
  };

  const handleBook = async payNow => {
    const [inStr, outStr] = window.split(" – ");
    const checkIn  = inStr  && new Date(inStr).toISOString().split("T")[0];
    const checkOut = outStr && new Date(outStr).toISOString().split("T")[0];

    try {
      const parts = [
        postOne(hotel , "HOTEL" , checkIn , checkOut),
        flight && postOne(flight, "FLIGHT", null    , null     ),
        event  && postOne(event , "EVENT" , null    , null     )
      ].filter(Boolean);

      const results = await Promise.all(parts);
      for (let r of results) {
        if (!r.ok) throw new Error(`Error ${r.status}`);
      }

      setError(null);
      setIsModalOpen(false);
      alert("Booking confirmed! Check your email.");

    } catch(err) {
      setError(err.message);
    }
  };

  return (
    <Wrapper>
      <Banner $gradient={gradient}>
        <BannerIcon>{icon}</BannerIcon>
        <BannerTitle>{hotel.title}</BannerTitle>
      </Banner>

      <Box>
        <Label>🏨 Hotel</Label>
        {hotel.city} · {nights} nights · €
        {(nights*hotel.pricePerNight).toFixed(0)}
        <br/> {hotel.description}
      </Box>

      <Box>
        <Label>✈ Flight</Label>
        {flight ? (
          <>
            {flight.departure} → {flight.arrival}<br/>
            Departs {new Date(flight.departureTime).toLocaleString()}<br/>
            Returns {new Date(flight.arrivalTime).toLocaleString()}<br/>
            €{flight.price}
          </>
        ) : <NotIncluded>No flight included.</NotIncluded>}
      </Box>

      <Box>
        <Label>🎫 Event</Label>
        {event ? (
          <>
            {event.title} – {new Date(event.eventDate).toLocaleString()}<br/>
            Venue {event.venue} · €{event.price}
          </>
        ) : <NotIncluded>No event included.</NotIncluded>}
      </Box>

      <H2>Total €{total.toFixed(0)}</H2>

      <p>
        Guests:{" "}
        <GuestInput
          type="number"
          min={1}
          value={guests}
          onChange={e=>setGuests(Math.max(1, +e.target.value||1))}
        />
      </p>

      <ConfirmBtn onClick={()=>setIsModalOpen(true)}>Book Now</ConfirmBtn>

      <Modal $isOpen={isModalOpen}>
        <ModalContent>
          <h3>Payment option</h3>
          <div style={{ display:"flex", gap:"1rem" }}>
            <PayNowBtn onClick={()=>handleBook(true)}>Pay Now</PayNowBtn>
            <PayOnArrivalBtn onClick={()=>handleBook(false)}>Pay on Arrival</PayOnArrivalBtn>
          </div>
          <button style={{ marginTop:"1rem" }}
                  onClick={()=>setIsModalOpen(false)}>
            Cancel
          </button>
          {error && <ErrorMessage>{error}</ErrorMessage>}
        </ModalContent>
      </Modal>
    </Wrapper>
  );
}
