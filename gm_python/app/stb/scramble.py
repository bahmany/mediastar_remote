from __future__ import annotations


def scramble_stb_info_for_broadcast(send_buff: bytearray, buff_length: int, xor_value: int = 0x5B) -> None:
    half = buff_length // 2
    for i in range(half):
        j = buff_length - 1 - i
        temp = send_buff[j]
        send_buff[j] = send_buff[i]
        send_buff[i] = temp
        send_buff[i] = (send_buff[i] ^ xor_value) & 0xFF
        send_buff[j] = (send_buff[j] ^ xor_value) & 0xFF

    if buff_length % 2:
        mid = buff_length // 2
        send_buff[mid] = (send_buff[mid] ^ xor_value) & 0xFF
