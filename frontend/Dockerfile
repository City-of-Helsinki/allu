FROM node:20.19.4
ENV NODE_ENV=dev

WORKDIR /allu-frontend

COPY ["package.json", "./"]

RUN npm install

COPY . .

CMD [ "npm", "run", "hmr" ]
