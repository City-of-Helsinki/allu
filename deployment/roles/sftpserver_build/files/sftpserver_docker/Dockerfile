FROM atmoz/sftp

ARG SFTP_CUSTOMER_PASS
ARG SFTP_INVOICE_PASS

RUN mkdir -p /etc/sftp/

RUN echo "allu:$SFTP_CUSTOMER_PASS:1001:100" > /etc/sftp/users.conf
RUN echo "allu:$SFTP_INVOICE_PASS:1001:100" >> /etc/sftp/users.conf
COPY sshd_config /etc/ssh/sshd_config
VOLUME /etc/ssh