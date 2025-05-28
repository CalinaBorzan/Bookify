
import React, { useEffect, useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import axios from "axios";
import { DateRange } from "react-date-range";
import { addDays, format, isWithinInterval, parseISO, differenceInDays, min, max } from "date-fns";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";

/* ---------- destination images (same as HomePage) ---------- */
const destinations = [
  { c: "FR", n: "France", img: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=1600&auto=format&q=80" },
  { c: "ES", n: "Spain", img: "/destinations/barcelona.jpg" },
  { c: "IT", n: "Italy", img: "/destinations/italy.jpg" },
  { c: "GR", n: "Greece", img: "/destinations/greece.jpg" },
  { c: "JP", n: "Japan", img: "/destinations/japan.jpg" },
  { c: "US", n: "USA", img: "/destinations/usa.jpg" },
  { c: "BR", n: "Brazil", img: "/destinations/brazil.jpg" },
  { c: "TH", n: "Thailand", img: "/destinations/thailand.jpg" },
];

/* ---------- utils ---------- */
const toJsDate = (s) => {
  if (!s) return null;
  try {
    return s.includes("T") ? parseISO(s) : parseISO(s.replace(" ", "T"));
  } catch (e) {
    console.error(`Invalid date format: ${s}`, e);
    return null;
  }
};

const nightsBetween = (start, end) => {
  if (!start || !end) return 1;
  return Math.max(1, Math.round(differenceInDays(end, start)));
};

const fmt = (d) => format(d, "d MMM yyyy");

const getHotelStayRange = (from, to, availableFrom, availableTo, maxNights = 14) => {
  if (!from || !to || !availableFrom || !availableTo) return { start: from, end: to, nights: 1 };
  const start = max([from, availableFrom]);
  const maxEnd = min([to, availableTo]);
  const earliestEnd = addDays(start, maxNights);
  const end = min([maxEnd, earliestEnd]);
  const nights = nightsBetween(start, end);
  return { start, end, nights };
};

const getEventStayRange = (eventDate, from, to, availableFrom, availableTo, maxNights = 14) => {
  if (!eventDate || !from || !to || !availableFrom || !availableTo) return { start: from, end: to, nights: 1 };
  const halfNights = Math.floor(maxNights / 2);
  let start = addDays(eventDate, -halfNights);
  let end = addDays(start, maxNights);
  start = max([start, from, availableFrom]);
  end = min([end, to, availableTo]);
  const nights = nightsBetween(start, end);
  return { start, end, nights };
};

/* ---------- styled bits ---------- */
const fadeIn = keyframes`from{opacity:0;transform:translateY(10px);}to{opacity:1;transform:translateY(0);}`;

const Hero = styled.section`
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.3)), url(${p => p.$img}) center/cover no-repeat;
  height: 300px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.8rem;
  font-weight: 800;
  text-transform: uppercase;
  color: #fff;
  text-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
  margin: 0 1rem;
  @media (max-width: 768px) {
    height: 200px;
    font-size: 2rem;
  }
`;

const Filters = styled.div`
  width: 90%;
  max-width: 1000px;
  margin: -60px auto 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  justify-content: center;
  animation: ${fadeIn} 0.5s ease-out;
  @media (max-width: 768px) {
    flex-direction: column;
    margin: -40px auto 1.5rem;
    padding: 1rem;
  }
`;

const FilterItem = styled.label`
  display: flex;
  flex-direction: column;
  font-weight: 600;
  color: #003b70;
  font-size: 0.95rem;
  flex: 1;
  min-width: 160px;
  max-width: 200px;
  input,
  select {
    margin-top: 0.5rem;
    padding: 0.7rem 1rem;
    font-size: 1rem;
    border: 1px solid #d1e0ff;
    border-radius: 10px;
    background: #f7faff;
    transition: all 0.3s ease;
    &:hover {
      border-color: #175dff;
      transform: scale(1.02);
    }
    &:focus {
      outline: none;
      border-color: #175dff;
      box-shadow: 0 0 8px rgba(23, 93, 255, 0.3);
    }
  }
`;

const CalendarWrapper = styled.div`
  flex: 2;
  max-width: 600px;
  margin: 0 auto;
  .rdrCalendarWrapper {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background: #fff;
  }
  .rdrDefinedRangesWrapper {
    display: none;
  }
  .rdrDayHovered {
    background: #e6f0ff;
  }
  .rdrDayStartOfMonth,
  .rdrDayEndOfMonth {
    color: #175dff;
  }
  @media (max-width: 768px) {
    max-width: 100%;
  }
`;

const Grid = styled.section`
  display: grid;
  gap: 1.5rem;
  max-width: 1200px;
  margin: 0 auto 3rem;
  padding: 0 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  animation: ${fadeIn} 0.6s ease-out;
`;

const Card = styled.div`
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: #fff;
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
  }
`;

const Banner = styled.div`
  height: 180px;
  background: ${p => p.$gradient};
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  border-bottom: 2px solid #e6f0ff;
  position: relative;
`;

const BannerIcon = styled.span`
  position: absolute;
  top: 1rem;
  left: 1rem;
  font-size: 2rem;
`;

const BannerTitle = styled.h3`
  margin: 0;
  font-size: 1.2rem;
  font-weight: 700;
  color: #fff;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  line-height: 1.4;
`;

const Glass = styled.div`
  padding: 1rem 1.2rem;
`;

const Row = styled.p`
  margin: 0.15rem 0;
  font-size: 0.9rem;
  color: #42526e;
  line-height: 1.4;
`;

const Chip = styled.span`
  display: inline-block;
  font-size: 0.7rem;
  font-weight: 700;
  color: #fff;
  padding: 0.2rem 0.7rem;
  border-radius: 12px;
  margin-right: 0.5rem;
  background: ${p => p.$bg};
`;

const Price = styled.div`
  font-weight: 700;
  font-size: 1.1rem;
  color: #175dff;
  margin-top: 0.5rem;
`;

const Empty = styled.p`
  grid-column: 1/-1;
  text-align: center;
  font-weight: 600;
  color: #003b70;
  font-size: 1.1rem;
`;

const ErrorMessage = styled.p`
  grid-column: 1/-1;
  text-align: center;
  font-weight: 600;
  color: #e92365;
  font-size: 1.1rem;
`;

const Loading = styled.p`
  grid-column: 1/-1;
  text-align: center;
  font-weight: 600;
  color: #003b70;
  font-size: 1.1rem;
`;

/* ---------- component ---------- */
export default function CountryPage() {
  const { code } = useParams();
  const navigate = useNavigate();

  /* calendar default → tomorrow .. +7d */
  const [range, setRange] = useState([
    {
      key: "selection",
      color: "#175dff",
      startDate: addDays(new Date(), 1),
      endDate: addDays(new Date(), 8),
    },
  ]);
  const [guests, setGuests] = useState(2);
  const [stars, setStars] = useState("ANY");
  const [hotels, setHotels] = useState([]);
  const [flights, setFlights] = useState([]);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  /* load data */
  useEffect(() => {
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const cfg = { headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` } };
        const [h, f, e] = await Promise.all([
          axios.get("/api/listings/hotels", cfg).catch((e) => {
            console.error("Hotels API error:", e);
            return { data: [] };
          }),
          axios.get("/api/listings/flights", cfg).catch((e) => {
            console.error("Flights API error:", e);
            return { data: [] };
          }),
          axios.get("/api/listings/events", cfg).catch((e) => {
            console.error("Events API error:", e);
            return { data: [] };
          }),
        ]);

        console.log("Raw API Data:", { hotels: h.data, flights: f.data, events: e.data });

        const validHotels = h.data
          .map((o) => ({
            ...o,
            pricePerNight: o.price,
            availableFrom: toJsDate(o.availableFrom),
            availableTo: toJsDate(o.availableTo),
          }))
          .filter((o) => o.availableFrom && o.availableTo && o.country);
        const validFlights = f.data
          .map((o) => ({
            ...o,
            departureTime: toJsDate(o.departureTime),
            arrivalTime: toJsDate(o.arrivalTime),
          }))
          .filter((o) => o.departureTime && o.arrivalTime && o.country);
        const validEvents = e.data
          .map((o) => ({
            ...o,
            eventDate: toJsDate(o.eventDate),
          }))
          .filter((o) => o.eventDate && o.country);

        console.log("Filtered Data:", {
          hotels: validHotels,
          flights: validFlights,
          events: validEvents,
        });

        setHotels(validHotels);
        setFlights(validFlights);
        setEvents(validEvents);
      } catch (err) {
        setError("Failed to load packages. Please check your connection or try again later.");
        console.error("API Fetch Error:", err);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  /* compute bundles */
  const bundles = useMemo(() => {
    const [{ startDate: from, endDate: to }] = range;
    if (!from || !to) {
      console.log("Invalid date range:", { from, to });
      return [];
    }

    /* filter hotels: exact star rating match */
    const hs = hotels.filter((h) => {
      const matchCountry = h.country === code;
      const matchStars = stars === "ANY" || h.starRating === +stars;
      const matchGuests = h.totalRooms >= guests;
      const matchDates =
        isWithinInterval(h.availableFrom, { start: from, end: to }) ||
        isWithinInterval(h.availableTo, { start: from, end: to }) ||
        (h.availableFrom <= from && h.availableTo >= to);
      return matchCountry && matchStars && matchGuests && matchDates;
    });

    /* filter flights: departure and arrival within range */
    const fs = flights.filter((f) => {
      const matchCountry = f.country === code;
      const matchGuests = f.seatCapacity >= guests;
      const matchDates =
        isWithinInterval(f.departureTime, { start: from, end: to }) &&
        isWithinInterval(f.arrivalTime, { start: from, end: to });
      return matchCountry && matchGuests && matchDates;
    });

    /* filter events: event date within range */
    const es = events.filter((ev) => {
      const matchCountry = ev.country === code;
      const matchGuests = ev.ticketCapacity >= guests;
      const matchDate = isWithinInterval(ev.eventDate, { start: from, end: to });
      return matchCountry && matchGuests && matchDate;
    });

    console.log("Filtered for", code, ":", { hotels: hs, flights: fs, events: es });

    /* create bundles: prioritize flight-driven packages */
    const out = [];

    /* flight-based bundles: hotel + flight, hotel + flight + event */
    fs.forEach((f) => {
      const fNights = nightsBetween(f.departureTime, f.arrivalTime);
      hs.forEach((h) => {
        if (h.availableFrom <= f.departureTime && h.availableTo >= f.arrivalTime) {
          // Hotel + Flight
          out.push({
            id: `h${h.id}-f${f.id}`,
            hotel: h,
            flight: f,
            event: null,
            nights: fNights,
            window: `${fmt(f.departureTime)} – ${fmt(f.arrivalTime)}`,
            total: fNights * h.pricePerNight + f.price,
          });

          // Hotel + Flight + Event
          es.forEach((ev) => {
            if (isWithinInterval(ev.eventDate, { start: f.departureTime, end: f.arrivalTime })) {
              out.push({
                id: `h${h.id}-f${f.id}-e${ev.id}`,
                hotel: h,
                flight: f,
                event: ev,
                nights: fNights,
                window: `${fmt(f.departureTime)} – ${fmt(f.arrivalTime)}`,
                total: fNights * h.pricePerNight + f.price + ev.price,
              });
            }
          });
        }
      });
    });

    /* hotel-only bundles: max 14 nights */
    hs.forEach((h) => {
      const { start: hStart, end: hEnd, nights: hNights } = getHotelStayRange(
        from,
        to,
        h.availableFrom,
        h.availableTo
      );
      out.push({
        id: `h${h.id}`,
        hotel: h,
        flight: null,
        event: null,
        nights: hNights,
        window: `${fmt(hStart)} – ${fmt(hEnd)}`,
        total: hNights * h.pricePerNight,
      });
    });

    /* hotel + event bundles: center around event date, max 14 nights */
    hs.forEach((h) => {
      es.forEach((ev) => {
        const { start: eStart, end: eEnd, nights: eNights } = getEventStayRange(
          ev.eventDate,
          from,
          to,
          h.availableFrom,
          h.availableTo
        );
        out.push({
          id: `h${h.id}-e${ev.id}`,
          hotel: h,
          flight: null,
          event: ev,
          nights: eNights,
          window: `${fmt(eStart)} – ${fmt(eEnd)}`,
          total: eNights * h.pricePerNight + ev.price,
        });
      });
    });

    console.log(
      "Generated Bundles:",
      out.map((b) => ({
        id: b.id,
        total: b.total,
        nights: b.nights,
        window: b.window,
        starRating: b.hotel.starRating,
      }))
    );

    return out.sort((a, b) => a.total - b.total).slice(0, 40);
  }, [code, range, guests, stars, hotels, flights, events]);

  /* hero image (same as HomePage) */
  const heroImg =
    destinations.find((d) => d.c === code)?.img ||
    `https://source.unsplash.com/1600x600/?${code},landmark`;

  /* ---------- render ---------- */
  return (
    <>
      <Hero $img={heroImg}>{code}</Hero>

      <Filters>
        <CalendarWrapper>
          <DateRange
            ranges={range}
            onChange={(r) => setRange([{ ...r.selection, color: "#175dff" }])}
            moveRangeOnFirstSelection={false}
            rangeColors={["#175dff"]}
          />
        </CalendarWrapper>

        <FilterItem>
          Travellers
          <input
            type="number"
            min={1}
            max={10}
            value={guests}
            onChange={(e) => setGuests(Math.max(1, Math.min(10, +e.target.value || 1)))}
          />
        </FilterItem>

        <FilterItem>
          Hotel rating
          <select value={stars} onChange={(e) => setStars(e.target.value)}>
            <option value="ANY">Any</option>
            <option value="3">3★</option>
            <option value="4">4★</option>
            <option value="5">5★</option>
          </select>
        </FilterItem>
      </Filters>

      <Grid>
        {loading ? (
          <Loading>Loading packages...</Loading>
        ) : error ? (
          <ErrorMessage>{error}</ErrorMessage>
        ) : bundles.length ? (
          bundles.map((b) => {
            const hasFlight = !!b.flight;
            const hasEvent = !!b.event;
            const gradient = hasFlight
              ? hasEvent
                ? "linear-gradient(135deg, #175dff, #e92365)" // Flight + Event
                : "linear-gradient(135deg, #175dff, #4b9bff)" // Flight
              : hasEvent
              ? "linear-gradient(135deg, #e92365, #ff6b9b)" // Event
              : "linear-gradient(135deg, #003b70, #4b9bff)"; // Hotel only
            const icon = hasFlight ? "✈" : hasEvent ? "🎫" : "🏨";

            return (
              <Card key={b.id} onClick={() => navigate(`/packages/${b.id}`, { state: b })}>
                <Banner $gradient={gradient}>
                  <BannerIcon>{icon}</BannerIcon>
                  <BannerTitle>{b.hotel.title}</BannerTitle>
                </Banner>
                <Glass>
                  <Row>
                    🗓 {b.window}
                    <br />
                    {b.nights} nights · {b.hotel.city}
                    <br />
                    {b.flight && <Chip $bg="#175dff">✈ {b.flight.airline}</Chip>}
                    {b.event && (
                      <Chip $bg="#e92365">
                        🎫 {b.event.title.length > 18 ? b.event.title.slice(0, 18) + "…" : b.event.title}
                      </Chip>
                    )}
                  </Row>
                  <Price>€ {b.total.toFixed(0)}</Price>
                </Glass>
              </Card>
            );
          })
        ) : (
          <Empty>No bundles match your filters. Try adjusting dates or filters.</Empty>
        )}
      </Grid>
    </>
  );
}
