FROM node:12.18
ENV NODE_ENV=dev

WORKDIR /allu-frontend

COPY ["package.json", "./"]

RUN npm install

COPY . .

CMD [ "npm", "run", "hmr" ]
