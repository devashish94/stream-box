const jwt = require("jsonwebtoken");

const token =
  "eyJhbGciOiJSUzI1NiIsImtpZCI6ImIyNjIwZDVlN2YxMzJiNTJhZmU4ODc1Y2RmMzc3NmMwNjQyNDlkMDQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI2Mzc4NDM1MzM2OC1qcWwza25kMm00ZWhmYzRwa2pycmk2MTI0YzA4bjFwdi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjYzNzg0MzUzMzY4LWpxbDNrbmQybTRlaGZjNHBranJyaTYxMjRjMDhuMXB2LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEwMDU4MDczNjczOTUxNjE3MTk2IiwiZW1haWwiOiJkZXZhc2hpc2hyb3kxNEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IjJMcGpmUUN6Qk4yclllbXdUUkZmWUEiLCJuYW1lIjoiRGV2YXNoaXNoIFJveSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NMdERaXzIyV1hhWDdjZm9WYmRCMFgwV2c4UnRUUDdzVVVoZDBacVluVmVUTktobW5Xaz1zOTYtYyIsImdpdmVuX25hbWUiOiJEZXZhc2hpc2giLCJmYW1pbHlfbmFtZSI6IlJveSIsImlhdCI6MTcyNjQyOTQ3MSwiZXhwIjoxNzI2NDMzMDcxfQ.VAVB2-QVMIt_D8qPDteHTZGGz0LOttcN_9aXiczSGui1_L0QdYPaG5-iaom389enkKSWvYR70WW1ivJTDYitw2tnYOMFavpW9PT1aiwh-dtHxYKF5AHj0RzQAbtZQlEkX0o6Mj2BqzG_1r9OPCSqVfgOskXvOX4gPbV0arAWYlVcw1STAhpIyyt_In31tiZCGe7MBY4YA2s4Anb2IBWSwC1eh7JS4R6dR82yB1ovnY2SIJy3XQWom3hQArJ93rYYJXdrvrOvMxrF0_npvvNX1DjUz65pEizst_QrQxis6m_r6hWe5E5Wwhb-YUkLU3fXJzKlxx7KW-pBAymg7DhXWA"; // Replace with the actual JWT token

const decoded = jwt.decode(token);

console.log("Decoded Payload:", decoded);
