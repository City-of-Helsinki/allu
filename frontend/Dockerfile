FROM node:20.11
ENV NODE_ENV=dev

WORKDIR /allu-frontend

COPY ["package.json", "package-lock.json", "./"]

RUN npm ci

COPY . .

CMD [ "npm", "run", "hmr" ]
