package com.imseam.chatlet;



/// <summary>
/// Interface of message filters. Both request filter and reponse filters are derived from
/// this interface.
/// </summary>
public interface IChatletFilter extends IInitable{

	void doFilter(IUserRequest request, IMessageSender sender, IChatletFilterChain filterChain);
}
