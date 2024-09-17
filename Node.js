const express = require('express');
const bodyParser = require('body-parser');
const twilio = require('twilio');

const app = express();
const port = 3000;

const ACCOUNT_SID = 'VA30cb163da1e8d8de5ff4c95f9862b9fc';
const AUTH_TOKEN = '12864d154edde28691d835c88afc5055';
const TWILIO_PHONE_NUMBER = '+918270426785';
const otps = {}; // In-memory storage for OTPs

const client = twilio(ACCOUNT_SID, AUTH_TOKEN);

app.use(bodyParser.json());

app.post('/send-otp', async (req, res) => {
    const phoneNumber = req.body.phone_number;
    const otp = Math.floor(100000 + Math.random() * 900000).toString(); // Generate 6-digit OTP
    otps[phoneNumber] = otp; // Save OTP for the phone number

    try {
        await client.messages.create({
            body: `Your OTP is ${otp}`,
            from: TWILIO_PHONE_NUMBER,
            to: phoneNumber
        });
        res.status(200).json({ success: true, message: 'OTP sent' });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to send OTP' });
    }
});

app.post('/verify-otp', (req, res) => {
    const phoneNumber = req.body.phone_number;
    const otp = req.body.otp;

    if (otps[phoneNumber] && otps[phoneNumber] === otp) {
        res.status(200).json({ success: true, message: 'OTP verified successfully' });
    } else {
        res.status(200).json({ success: false, message: 'Invalid OTP' });
    }
});

app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});

