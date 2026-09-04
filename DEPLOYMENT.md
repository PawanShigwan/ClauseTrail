# ClauseTrail – Deployment Guide

> **Frontend → Vercel** | **Backend → Render (Docker)** | **Database → MongoDB Atlas**

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Deploy Backend on Render](#2-deploy-backend-on-render)
3. [Deploy Frontend on Vercel](#3-deploy-frontend-on-vercel)
4. [Link Frontend ↔ Backend](#4-link-frontend--backend)
5. [Verify the deployment](#5-verify-the-deployment)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. Prerequisites

| Item | Notes |
|------|-------|
| GitHub repo | Push the entire monorepo to GitHub (both `Backend/` and `Frontend/` in one repo) |
| MongoDB Atlas | Keep using the existing Atlas cluster – no changes needed |
| Render account | Free plan works; upgrade to **Starter ($7/mo)** to avoid cold starts |
| Vercel account | Free Hobby plan is sufficient |

---

## 2. Deploy Backend on Render

### 2.1 Create a Web Service

1. Go to [render.com/dashboard](https://dashboard.render.com) → **New +** → **Web Service**
2. Connect your GitHub repository
3. Configure the service:

   | Setting | Value |
   |---------|-------|
   | **Name** | `clausetrail-backend` |
   | **Root Directory** | `Backend` |
   | **Runtime** | `Docker` |
   | **Dockerfile Path** | `Dockerfile` *(auto-detected)* |
   | **Branch** | `main` |
   | **Instance Type** | Free (or Starter for always-on) |

### 2.2 Set Environment Variables

In Render → Your Service → **Environment** tab, add the following **secret** environment variables:

| Variable | Value | Notes |
|----------|-------|-------|
| `MONGODB_URI` | `mongodb+srv://...` | Your Atlas connection string |
| `JWT_SECRET` | *(random hex 32 bytes)* | Generate: `openssl rand -hex 32` |
| `CORS_ALLOWED_ORIGINS` | *(set after Vercel deploy, step 4)* | Comma-separated list |
| `SPRING_PROFILES_ACTIVE` | `prod` | Optional profile flag |

> [!NOTE]
> Render automatically injects `PORT` (usually `10000`). Spring Boot reads it via `${PORT:8080}` in `application.yml`.

### 2.3 Health Check

After deployment, Render pings `GET /api/health`. It should return:

```json
{
  "status": "UP",
  "service": "ClauseTrail Backend API",
  "version": "1.0.0"
}
```

### 2.4 Note Your Backend URL

Once the service is live, copy the URL – it looks like:
```
https://clausetrail-backend.onrender.com
```
You'll need this in the next step.

---

## 3. Deploy Frontend on Vercel

### 3.1 Import the Project

1. Go to [vercel.com/dashboard](https://vercel.com/dashboard) → **Add New…** → **Project**
2. Connect your GitHub repository
3. Configure:

   | Setting | Value |
   |---------|-------|
   | **Framework Preset** | `Vite` |
   | **Root Directory** | `Frontend` |
   | **Build Command** | `npm run build` |
   | **Output Directory** | `dist` |
   | **Install Command** | `npm install` |

### 3.2 Set Environment Variables (add before first deploy)

In Vercel → Project → **Settings** → **Environment Variables**:

| Variable | Value |
|----------|-------|
| `VITE_API_URL` | `https://clausetrail-backend.onrender.com` *(your Render URL from step 2.4)* |

> [!IMPORTANT]
> `VITE_API_URL` is embedded at **build time** by Vite. If you change it, you must **redeploy** the frontend.

### 3.3 Deploy

Click **Deploy**. Vercel will:
- Install dependencies
- Run `tsc && vite build`
- Upload the `dist/` folder to the CDN

Your frontend URL will look like:
```
https://clausetrail.vercel.app
```

---

## 4. Link Frontend ↔ Backend

After both services are live, update the Render backend to allow your exact Vercel URL:

1. Render → Your Service → **Environment** → Update `CORS_ALLOWED_ORIGINS`:
   ```
   https://clausetrail.vercel.app,https://*.vercel.app
   ```
2. Click **Save Changes** – Render will automatically redeploy.

The wildcard `https://*.vercel.app` covers all Vercel **preview deployments** too (every PR gets a unique preview URL), so you can test API calls from PR previews.

---

## 5. Verify the Deployment

Run through this checklist after deploying:

- [ ] `GET https://clausetrail-backend.onrender.com/api/health` → returns `{"status":"UP"}`
- [ ] Frontend loads at your Vercel URL without errors
- [ ] Login / Register works (JWT round-trip)
- [ ] CORS: browser console shows no `Cross-Origin` errors
- [ ] File upload (contract) works (20 MB multipart limit set in `application.yml`)
- [ ] PDF export returns a blob successfully

---

## 6. Troubleshooting

### Cold Starts (Render Free Tier)
Render's free tier **spins down** services after 15 min of inactivity. The first request after sleep may take **30–60 seconds**. To avoid this, upgrade to the **Starter** plan.

### CORS Errors
- Verify `CORS_ALLOWED_ORIGINS` in Render includes your exact Vercel URL (no trailing slash).
- Check that `https://*.vercel.app` is included for preview deployments.
- The SecurityConfig uses `setAllowedOriginPatterns` which supports wildcards.

### Build Fails on Render (Out of Memory)
The free tier has 512 MB RAM. Maven's compile phase can be heavy. If builds fail, add:
```
MAVEN_OPTS=-Xmx384m
```
as an environment variable on Render.

### `VITE_API_URL` Not Working
- The variable must start with `VITE_` (Vite's convention for client-side env vars).
- Changing it requires a **redeploy** (Vite inlines it at build time, not runtime).
- In local dev, leave it unset — the `vite.config.ts` proxy handles routing.

### MongoDB Atlas Connection Issues
- Ensure your Atlas cluster's **Network Access** list includes `0.0.0.0/0` (allow all) or Render's outbound IPs.
- Render's free tier uses **dynamic IPs** (no fixed IP), so `0.0.0.0/0` is required.

---

## Quick Reference – Environment Variables

| Variable | Where | Required | Description |
|----------|-------|----------|-------------|
| `MONGODB_URI` | Render | ✅ | MongoDB Atlas connection string |
| `JWT_SECRET` | Render | ✅ | Random 32-byte hex string |
| `CORS_ALLOWED_ORIGINS` | Render | ✅ | Comma-separated allowed CORS origins |
| `PORT` | Render (auto) | ⚙️ | Injected by Render automatically |
| `SPRING_PROFILES_ACTIVE` | Render | ⬜ | e.g. `prod` |
| `JWT_EXPIRATION_MS` | Render | ⬜ | Token TTL (default: 86400000 = 24h) |
| `LOG_LEVEL_APP` | Render | ⬜ | App log level (default: `DEBUG`) |
| `VITE_API_URL` | Vercel | ✅ | Full Render backend URL (no trailing slash) |
