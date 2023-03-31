FROM node:14.21.3
ENV NODE_ENV=dev

WORKDIR /allu-frontend

COPY ["package.json", "./"]

RUN npm install

COPY . .

CMD [ "npm", "run", "hmr" ]
