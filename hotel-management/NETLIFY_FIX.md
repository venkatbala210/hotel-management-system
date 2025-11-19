# Netlify Build Fix

## Problem
Build command error: `hotel-management/frontend: Is a directory`

## Solution

### Option 1: Use netlify.toml (Recommended)

The `netlify.toml` file has been created in the repository root (`hotel-management/netlify.toml`).

1. **Commit and push the file:**
   ```bash
   git add hotel-management/netlify.toml
   git commit -m "Add netlify.toml for deployment"
   git push
   ```

2. **In Netlify Dashboard:**
   - Go to Site settings → Build & deploy
   - Clear/remove the manual build command (leave it empty)
   - Clear/remove the publish directory (leave it empty)
   - Netlify will automatically use `netlify.toml`

### Option 2: Manual Configuration (If netlify.toml doesn't work)

If you prefer to set it manually in Netlify UI:

1. **Base directory**: (leave empty)
2. **Build command**: `cd hotel-management/frontend && npm install && npm run build`
3. **Publish directory**: `hotel-management/frontend/build`
4. **Functions directory**: (leave empty or `netlify/functions`)

### Important Notes

- The build command MUST include `cd` to change directory
- Make sure `package.json` exists in `hotel-management/frontend/`
- The publish directory should point to where `npm run build` creates the `build` folder

## Environment Variables

Don't forget to set in Netlify Dashboard → Site settings → Environment variables:

```
REACT_APP_API_URL=https://hotel-management-system-5iby.onrender.com
```

Replace with your actual Render backend URL.

