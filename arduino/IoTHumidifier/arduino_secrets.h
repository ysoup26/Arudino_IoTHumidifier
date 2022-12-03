// Fill in  your WiFi networks SSID and password
#define SECRET_SSID "wifi-name"
#define SECRET_PASS "wifi-password"

// Fill in the hostname of your AWS IoT broker
#define SECRET_BROKER "~~.iot.us-east-1.amazonaws.com"//aws iot>설정>엔드포인트

//AWS iot와 통신시 인증하는 부분
//aws iot>보안>인증서>xxxxxxxxxx-certificate.perm.crt의 내용 복붙
const char SECRET_CERTIFICATE[] = R"(
-----BEGIN CERTIFICATE-----
MIIChTCCAW2gAwIBAgIUDM5pY/4dvr6NNAyyWvO10ISBAQkwDQYJKoZIhvcNAQEL
BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g
SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIyMTEyOTEyMzYw
NVoXDTQ5MTIzMTIzNTk1OVowFTETMBEGA1UEAxMKSHVtaWRpZmllcjBZMBMGByqG
SM49AgEGCCqGSM49AwEHA0IABLJ+zRNcBgoHI9n2aApyPVcLD1Uc2kU6jKc0a3nA
AT/QkdyIFt6a9DH3oXkgxas7JfyNgVTtguPgeWUXpQ/mnJqjYDBeMB8GA1UdIwQY
MBaAFJgSEl5ATB2DGVbMQpDwhE5469iYMB0GA1UdDgQWBBTDd4w8FCNtlh7nshqV
wfVVxFSkWTAMBgNVHRMBAf8EAjAAMA4GA1UdDwEB/wQEAwIHgDANBgkqhkiG9w0B
AQsFAAOCAQEAGqnmL2qQJgQy7n2+Z626BdvZDXffzoZlctSh4c4Ig8CUg3U1Bsq8
ZciuEI+EWdSj9ep87mO0gwYZhez1Y4xgFQzsyWxcpYQfNJ+Tvbq06pPvAWdJYZUH
t9agNANxozIB6w3vkShscZ3ZH04amG1mQr9ou12XUud9SZq188g4fgv5Q+07XkT6
/lRUaAy+zd4bWXw+A2sLYaIwapBqGpEGJeXNgkhGNe7Vf+53FTOUFThtzPGG7dpD
8WAT33y5DyiGMmmPnpnyxHTPFLQdcumjRFTPgkfhSaeqZRRaCln0gp3Nm7nXv+UY
UwqR7v5vzlonF1id7mv0UlVGwWE64v/hWQ==
-----END CERTIFICATE-----
)";
